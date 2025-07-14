package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ProductDTO;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProductDAO implements GenericDAO<ProductDTO, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductId", "SKU", "ProductName", "BasePrice", "CurrentPrice", "StockQuantity", "ProductType", "CreatedAt", "LastUpdated", "IsActive"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductId";

    private final DataSource dataSource;

    public ProductDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(ProductDTO productDTO) throws SQLException {
        validateProduct(productDTO);

        String sql;
        if (productDTO.getProductId() == 0) {
            sql = "INSERT INTO Product (ProductId, SKU, ProductName, BasePrice, CurrentPrice, StockQuantity, ProductType, CreatedAt, LastUpdated, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productDTO.getProductId());
                ps.setString(2, productDTO.getSku());
                ps.setString(3, productDTO.getProductName());
                ps.setBigDecimal(4, productDTO.getBasePrice());
                ps.setBigDecimal(5, productDTO.getCurrentPrice());
                ps.setDouble(6, productDTO.getStockQuantity());
                ps.setString(7, productDTO.getProductType().name());
                ps.setTimestamp(8, (productDTO.getCreatedAt() != null) ? Timestamp.valueOf(productDTO.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(9, (productDTO.getLastUpdated() != null) ? Timestamp.valueOf(productDTO.getLastUpdated()) : null);
                ps.setBoolean(10, productDTO.isActive());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productDTO.setProductId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating product failed, no ID obtained.");
                    }
                }
            }
        } else {
            sql = "UPDATE Product SET SKU = ?, ProductName = ?, BasePrice = ?, CurrentPrice = ?, StockQuantity = ?, ProductType = ?, CreatedAt = ?, LastUpdated = ?, IsActive = ? WHERE ProductId = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, productDTO.getSku());
                ps.setString(2, productDTO.getProductName());
                ps.setBigDecimal(3, productDTO.getBasePrice());
                ps.setBigDecimal(4, productDTO.getCurrentPrice());
                ps.setDouble(5, productDTO.getStockQuantity());
                ps.setString(6, productDTO.getProductType().name());
                ps.setTimestamp(7, (productDTO.getCreatedAt() != null) ? Timestamp.valueOf(productDTO.getCreatedAt()) : null);
                ps.setTimestamp(8, (productDTO.getLastUpdated() != null) ? Timestamp.valueOf(productDTO.getLastUpdated()) : null);
                ps.setBoolean(9, productDTO.isActive());
                ps.setInt(10, productDTO.getProductId());

                ps.executeUpdate();
            }
        }
    }

    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product ID cannot be null or zero.");
        }

        String sql = "DELETE FROM Product WHERE ProductId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public ProductDTO getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ProductID must be a positive integer.");
        }
        String sql = "SELECT * FROM Product WHERE ProductId = ?";
        ProductDTO productDTO = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                productDTO = extractProductFromResultSet(rs);
            }
        }
        return productDTO;
    }


    public Collection<ProductDTO> getAll(String order) throws SQLException {
        Collection<ProductDTO> productDTOS = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Product");

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
                productDTOS.add(extractProductFromResultSet(rs));
            }
        }
        return productDTOS;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);

    }

    private ProductDTO extractProductFromResultSet(ResultSet rs) throws SQLException {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(rs.getInt("ProductId"));
        productDTO.setSku(rs.getString("SKU"));
        productDTO.setProductName(rs.getString("ProductName"));
        productDTO.setBasePrice(rs.getBigDecimal("BasePrice"));
        productDTO.setCurrentPrice(rs.getBigDecimal("CurrentPrice"));
        productDTO.setStockQuantity(rs.getInt("StockQuantity"));
        productDTO.setProductType(ProductDTO.ProductType.valueOf(rs.getString("ProductType")));
        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            productDTO.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        Timestamp lastUpdatedTimestamp = rs.getTimestamp("LastUpdated");
        if (lastUpdatedTimestamp != null) {
            productDTO.setLastUpdated(lastUpdatedTimestamp.toLocalDateTime());
        }
        productDTO.setActive(rs.getBoolean("IsActive"));

        return productDTO;
    }

    private void validateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("Il prodotto non può essere null.");
        }
        if (productDTO.getSku() == null || productDTO.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU non può essere null o vuoto.");
        }
        if (productDTO.getProductName() == null || productDTO.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del prodotto non può essere null o vuoto.");
        }
        if (productDTO.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Prezzo base deve essere positivo");
        }
        if (productDTO.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Prezzo corrente deve essere positivo");
        }
        if (productDTO.getStockQuantity() < 0) {
            throw new IllegalArgumentException("La stock quantity deve essere positiva o zero.");
        }
        if (productDTO.getProductType() == null) {
            throw new IllegalArgumentException("Il tipo di prodotto non può essere null.");
        }

    }


    public Collection<ProductDTO> getProductsByCategory(int categoryId) throws SQLException {
        Collection<ProductDTO> products = new ArrayList<>();
        String sql = """
                SELECT *
                FROM Product
                JOIN ProductCategory ON Product.ProductId = ProductCategory.ProductID
                WHERE ProductCategory.CategoryID = ? AND Product.IsActive = true
                ORDER BY Product.ProductName
                """;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDTO product = extractProductFromResultSet(rs);
                    products.add(product);
                }
            }
        }
        return products;
    }
}
