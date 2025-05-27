package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProductDAO implements GenericDAO<Product, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductId", "SKU", "ProductName", "BasePrice", "CurrentPrice", "StockQuantity", "ProductType", "CreatedAt", "LastUpdated", "IsActive"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductId";

    private final DataSource dataSource;

    public ProductDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(Product product) throws SQLException {
        validateProduct(product);

        String sql;
        if (product.getProductId() == 0) {
            sql = "INSERT INTO Product (ProductId, SKU, ProductName, BasePrice, CurrentPrice, StockQuantity, ProductType, CreatedAt, LastUpdated, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, product.getProductId());
                ps.setString(2, product.getSku());
                ps.setString(3, product.getProductName());
                ps.setDouble(4, product.getBasePrice());
                ps.setDouble(5, product.getCurrentPrice());
                ps.setDouble(6, product.getStockQuantity());
                ps.setString(7, product.getProductType().name());
                ps.setTimestamp(8, (product.getCreatedAt() != null) ? Timestamp.valueOf(product.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(9, (product.getLastUpdated() != null) ? Timestamp.valueOf(product.getLastUpdated()) : null);
                ps.setBoolean(10, product.isActive());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating product failed, no ID obtained.");
                    }
                }
            }
        } else {
            sql = "UPDATE Product SET SKU = ?, ProductName = ?, BasePrice = ?, CurrentPrice = ?, StockQuantity = ?, ProductType = ?, CreatedAt = ?, LastUpdated = ?, IsActive = ? WHERE ProductId = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, product.getSku());
                ps.setString(2, product.getProductName());
                ps.setDouble(3, product.getBasePrice());
                ps.setDouble(4, product.getCurrentPrice());
                ps.setDouble(5, product.getStockQuantity());
                ps.setString(6, product.getProductType().name());
                ps.setTimestamp(7, (product.getCreatedAt() != null) ? Timestamp.valueOf(product.getCreatedAt()) : null);
                ps.setTimestamp(8, (product.getLastUpdated() != null) ? Timestamp.valueOf(product.getLastUpdated()) : null);
                ps.setBoolean(9, product.isActive());

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

    public Product getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ProductID must be a positive integer.");
        }
        String sql = "SELECT * FROM Product WHERE ProductId = ?";
        Product product = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                product = extractProductFromResultSet(rs);
            }
        }
        return product;
    }

    public Collection<Product> getAll(String order) throws SQLException {
        Collection<Product> products = new ArrayList<>();
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
                products.add(extractProductFromResultSet(rs));
            }
        }
        return products;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return ALLOWED_ORDER_COLUMNS;

    }

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("ProductId"));
        product.setSku(rs.getString("SKU"));
        product.setProductName(rs.getString("ProductName"));
        product.setBasePrice(rs.getDouble("BasePrice"));
        product.setCurrentPrice(rs.getDouble("CurrentPrice"));
        product.setStockQuantity(rs.getInt("StockQuantity"));
        product.setProductType(Product.ProductType.valueOf(rs.getString("ProductType")));
        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            product.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        Timestamp lastUpdatedTimestamp = rs.getTimestamp("LastUpdated");
        if (lastUpdatedTimestamp != null) {
            product.setLastUpdated(lastUpdatedTimestamp.toLocalDateTime());
        }
        product.setActive(rs.getBoolean("IsActive"));

        return product;
    }

    private void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (product.getSku() == null || product.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty.");
        }
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product Name cannot be null or empty.");
        }
        if (product.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base Price must be positive.");
        }
        if (product.getCurrentPrice() <= 0) {
            throw new IllegalArgumentException("Current Price must be positive.");
        }
        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock Quantity cannot be negative.");
        }
        if (product.getProductType() == null) {
            throw new IllegalArgumentException("Product Type cannot be null.");
        }
        if (product.getCreatedAt() == null) {
            throw new IllegalArgumentException("CreatedAt timestamp cannot be null.");
        }

    }
}
