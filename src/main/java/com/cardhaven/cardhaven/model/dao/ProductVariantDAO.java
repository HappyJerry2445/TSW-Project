package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.ProductVariant;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class ProductVariantDAO implements GenericDAO<ProductVariant, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
           "VariantId", "ProductId", "VariantName", "Attributes", "AdditionalPrice", "Stock"
    );
    private static final String DEFAULT_ORDER_COLUMN = "VariantId";

    private final DataSource dataSource;

    public ProductVariantDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource,"DataSource cannot be null.");
    }

    public void save(ProductVariant productVariant) throws SQLException {
        validateProductVariant(productVariant);

        String sql;
        if(productVariant.getVariantId() == 0) {
            sql = "INSERT INTO ProductVariant (VariantId, ProductId, VariantName, Attributes, AdditionalPrice, Stock) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productVariant.getVariantId());
                ps.setInt(2, productVariant.getProductId());
                ps.setString(3, productVariant.getVariantName());
                ps.setString(4, productVariant.getAttributes());
                ps.setDouble(5, productVariant.getAdditionalPrice());
                ps.setInt(6, productVariant.getStock());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product variant.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productVariant.setVariantId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating product variant failed, no ID obtained.");
                    }
                }
            }
        } else {
            sql = "UPDATE ProductVariant SET ProductId = ?, VariantName = ?, Attributes = ?, AdditionalPrice = ?, Stock = ? WHERE VariantId = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productVariant.getProductId());
                ps.setString(2, productVariant.getVariantName());
                ps.setString(3, productVariant.getAttributes());
                ps.setDouble(4, productVariant.getAdditionalPrice());
                ps.setInt(5, productVariant.getStock());

                ps.executeUpdate();
            }
        }
    }

    public boolean delete(Integer id) throws SQLException {
        if(id == null || id <= 0) {
            throw new IllegalArgumentException("Product Variant id cannot be null or zero.");
        }

        String sql = "DELETE FROM ProductVariant WHERE VariantId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public ProductVariant getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product Variant id must be a positive integer.");
        }
        String sql = "SELECT * FROM ProductVariant WHERE VariantId = ?";
        ProductVariant productVariant = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                productVariant = extractProductVariantFromResulSet(rs);
            }
            return productVariant;
        }
    }

    public Collection<ProductVariant> getAll(String order) throws SQLException {
        Collection<ProductVariant> productVariants = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ProductVariant");

        String actualOrderColumn = DEFAULT_ORDER_COLUMN;
        if (order != null && !order.trim().isEmpty()) {
            String trimmedOrder = order.trim();
            if (ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
                actualOrderColumn = trimmedOrder;
            } else {
                System.err.println("Warning: Attempted to order by invalid column: '" + order + "'. Falling back to default order.");
            }
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productVariants.add(extractProductVariantFromResulSet(rs));
            }
        }
        return productVariants;
    }

    public List<String> getAllowedOrderColumns() {
        return ALLOWED_ORDER_COLUMNS;
    }

    private ProductVariant extractProductVariantFromResulSet(ResultSet rs) throws SQLException {
        ProductVariant productVariant = new ProductVariant();
        productVariant.setVariantId(rs.getInt("VariantId"));
        productVariant.setProductId(rs.getInt("ProductId"));
        productVariant.setVariantName(rs.getString("VariantName"));
        productVariant.setAttributes(rs.getString("Attributes"));
        productVariant.setAdditionalPrice(rs.getDouble("AdditionalPrice"));
        productVariant.setStock(rs.getInt("Stock"));

        return productVariant;
    }

    private void validateProductVariant(ProductVariant productVariant) {
        if(productVariant == null) {
            throw new IllegalArgumentException("Product variant cannot be null.");
        }

        if (productVariant.getProductId() <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive integer.");
        }

        if (productVariant.getVariantName() == null || productVariant.getVariantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Variant name cannot be null or empty.");
        }

        if (productVariant.getAttributes() == null) {
            throw new IllegalArgumentException("Attributes cannot be null.");
        }

        if (productVariant.getAdditionalPrice() < 0) {
            throw new IllegalArgumentException("Additional price cannot be negative.");
        }

        if (productVariant.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }
}
