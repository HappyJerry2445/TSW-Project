package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ProductVariantDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class ProductVariantDAO implements GenericDAO<ProductVariantDTO, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "VariantId", "ProductId", "VariantName", "Attributes", "AdditionalPrice", "Stock"
    );
    private static final String DEFAULT_ORDER_COLUMN = "VariantId";

    private final DataSource dataSource;

    public ProductVariantDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(ProductVariantDTO productVariantDTO) throws SQLException {
        validateProductVariant(productVariantDTO);

        String sql;
        if (productVariantDTO.getVariantId() == 0) {
            sql = "INSERT INTO ProductVariant (VariantId, ProductId, VariantName, Attributes, AdditionalPrice, Stock) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productVariantDTO.getVariantId());
                ps.setInt(2, productVariantDTO.getProductId());
                ps.setString(3, productVariantDTO.getVariantName());
                ps.setString(4, productVariantDTO.getAttributes());
                ps.setDouble(5, productVariantDTO.getAdditionalPrice());
                ps.setInt(6, productVariantDTO.getStock());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product variant.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productVariantDTO.setVariantId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating product variant failed, no ID obtained.");
                    }
                }
            }
        } else {
            sql = "UPDATE ProductVariant SET ProductId = ?, VariantName = ?, Attributes = ?, AdditionalPrice = ?, Stock = ? WHERE VariantId = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productVariantDTO.getProductId());
                ps.setString(2, productVariantDTO.getVariantName());
                ps.setString(3, productVariantDTO.getAttributes());
                ps.setDouble(4, productVariantDTO.getAdditionalPrice());
                ps.setInt(5, productVariantDTO.getStock());

                ps.executeUpdate();
            }
        }
    }

    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product Variant id cannot be null or zero.");
        }

        String sql = "DELETE FROM ProductVariant WHERE VariantId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public ProductVariantDTO getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product Variant id must be a positive integer.");
        }
        String sql = "SELECT * FROM ProductVariant WHERE VariantId = ?";
        ProductVariantDTO productVariantDTO = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                productVariantDTO = extractProductVariantFromResulSet(rs);
            }
            return productVariantDTO;
        }
    }

    public Collection<ProductVariantDTO> getAll(String order) throws SQLException {
        Collection<ProductVariantDTO> productVariantDTOS = new ArrayList<>();
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
                productVariantDTOS.add(extractProductVariantFromResulSet(rs));
            }
        }
        return productVariantDTOS;
    }

    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    private ProductVariantDTO extractProductVariantFromResulSet(ResultSet rs) throws SQLException {
        ProductVariantDTO productVariantDTO = new ProductVariantDTO();
        productVariantDTO.setVariantId(rs.getInt("VariantId"));
        productVariantDTO.setProductId(rs.getInt("ProductId"));
        productVariantDTO.setVariantName(rs.getString("VariantName"));
        productVariantDTO.setAttributes(rs.getString("Attributes"));
        productVariantDTO.setAdditionalPrice(rs.getDouble("AdditionalPrice"));
        productVariantDTO.setStock(rs.getInt("Stock"));

        return productVariantDTO;
    }

    private void validateProductVariant(ProductVariantDTO productVariantDTO) {
        if (productVariantDTO == null) {
            throw new IllegalArgumentException("Product variant cannot be null.");
        }

        if (productVariantDTO.getProductId() <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive integer.");
        }

        if (productVariantDTO.getVariantName() == null || productVariantDTO.getVariantName().trim().isEmpty()) {
            throw new IllegalArgumentException("Variant name cannot be null or empty.");
        }

        if (productVariantDTO.getAttributes() == null) {
            throw new IllegalArgumentException("Attributes cannot be null.");
        }

        if (productVariantDTO.getAdditionalPrice() < 0) {
            throw new IllegalArgumentException("Additional price cannot be negative.");
        }

        if (productVariantDTO.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }
}
