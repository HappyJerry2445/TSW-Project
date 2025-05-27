// src/main/java/com/cardhaven/cardhaven/model/dao/AccessoryDAO.java
package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Accessory;
import com.cardhaven.cardhaven.model.beans.Accessory.AccessoryType;
import com.cardhaven.cardhaven.model.beans.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class AccessoryDAO implements GenericDAO<Accessory, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductId", "SKU", "ProductName", "BasePrice", "CurrentPrice", "StockQuantity",
            "AccessoryID", "AccessoryType", "Material", "Color", "Dimensions", "Compatibility",
            "CreatedAt", "LastUpdated", "IsActive"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductId";

    private final DataSource dataSource;

    /**
     * Constructor for AccessoryDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public AccessoryDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves an Accessory bean to the database.
     * If the ProductId (AccessoryID) is 0, it performs an INSERT operation (first to Product, then to Accessory).
     * Otherwise, it performs an UPDATE operation (first to Product, then to Accessory).
     * This method manages its own transaction.
     *
     * @param accessory The Accessory object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if accessory or essential fields are null/empty.
     */
    @Override
    public void save(Accessory accessory) throws SQLException {
        validateAccessory(accessory);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction for atomicity

            // 1. Save the Product part (INSERT or UPDATE)
            String productSql;
            if (accessory.getProductId() == 0) { // New Product (and Accessory)
                productSql = "INSERT INTO Product (SKU, ProductName, BasePrice, CurrentPrice, StockQuantity, ProductType, CreatedAt, LastUpdated, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, accessory.getSku());
                    ps.setString(2, accessory.getProductName());
                    ps.setDouble(3, accessory.getBasePrice());
                    ps.setDouble(4, accessory.getCurrentPrice());
                    ps.setDouble(5, accessory.getStockQuantity());
                    ps.setString(6, Product.ProductType.Accessory.name()); // Force ProductType to Accessory
                    ps.setTimestamp(7, (accessory.getCreatedAt() != null) ? Timestamp.valueOf(accessory.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(8, (accessory.getLastUpdated() != null) ? Timestamp.valueOf(accessory.getLastUpdated()) : null);
                    ps.setBoolean(9, accessory.isActive());

                    int affectedRows = ps.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating product failed, no rows affected.");
                    }

                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            accessory.setProductId(generatedKeys.getInt(1)); // Set the generated Product ID
                        } else {
                            throw new SQLException("Creating product failed, no ID obtained.");
                        }
                    }
                }
            } else { // Existing Product (UPDATE)
                productSql = "UPDATE Product SET SKU = ?, ProductName = ?, BasePrice = ?, CurrentPrice = ?, StockQuantity = ?, ProductType = ?, LastUpdated = ?, IsActive = ? WHERE ProductId = ?";
                try (PreparedStatement ps = connection.prepareStatement(productSql)) {
                    ps.setString(1, accessory.getSku());
                    ps.setString(2, accessory.getProductName());
                    ps.setDouble(3, accessory.getBasePrice());
                    ps.setDouble(4, accessory.getCurrentPrice());
                    ps.setDouble(5, accessory.getStockQuantity());
                    ps.setString(6, Product.ProductType.Accessory.name()); // Force ProductType to Accessory
                    ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // Update LastUpdated
                    ps.setBoolean(8, accessory.isActive());
                    ps.setInt(9, accessory.getProductId());
                    ps.executeUpdate();
                }
            }

            // 2. Save the Accessory specific part (INSERT or UPDATE)
            String accessorySql;
            if (getByIdInternal(accessory.getProductId(), connection) == null) { // Check if Accessory entry exists for this ProductId
                accessorySql = "INSERT INTO Accessory (AccessoryID, AccessoryType, Material, Color, Dimensions, Compatibility) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(accessorySql)) {
                    ps.setInt(1, accessory.getProductId()); // Use the Product ID as AccessoryID
                    ps.setString(2, accessory.getAccessoryType().name());
                    setNullableString(ps, 3, accessory.getMaterial());
                    setNullableString(ps, 4, accessory.getColor());
                    setNullableString(ps, 5, accessory.getDimensions());
                    setNullableString(ps, 6, accessory.getCompatibility());
                    ps.executeUpdate();
                }
            } else { // Existing Accessory (UPDATE)
                accessorySql = "UPDATE Accessory SET AccessoryType = ?, Material = ?, Color = ?, Dimensions = ?, Compatibility = ? WHERE AccessoryID = ?";
                try (PreparedStatement ps = connection.prepareStatement(accessorySql)) {
                    ps.setString(1, accessory.getAccessoryType().name());
                    setNullableString(ps, 2, accessory.getMaterial());
                    setNullableString(ps, 3, accessory.getColor());
                    setNullableString(ps, 4, accessory.getDimensions());
                    setNullableString(ps, 5, accessory.getCompatibility());
                    ps.setInt(6, accessory.getProductId()); // Use the Product ID as AccessoryID for WHERE clause
                    ps.executeUpdate();
                }
            }
            connection.commit(); // Commit transaction if all successful
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Rollback on error
            }
            System.err.println("Error saving Accessory: " + e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close(); // Close connection
            }
        }
    }

    /**
     * Deletes an Accessory from the database by its ID (which is also ProductID).
     * This method manages its own transaction and explicitly deletes from both tables.
     *
     * @param id The ID (which is also ProductID) of the Accessory to delete.
     * @return true if the Accessory was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is null or zero.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("ID cannot be null or zero for deletion.");
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction for atomicity

            // Delete from Accessory table first
            String deleteAccessorySql = "DELETE FROM Accessory WHERE AccessoryID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteAccessorySql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Then delete from Product table
            String deleteProductSql = "DELETE FROM Product WHERE ProductId = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteProductSql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    connection.rollback(); // Rollback if product not deleted.
                    return false;
                }
            }
            connection.commit(); // Commit transaction if both successful
            return true;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Rollback on error
            }
            System.err.println("Error deleting Accessory with ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close(); // Close connection
            }
        }
    }

    /**
     * Retrieves an Accessory by its ProductID (which is also the AccessoryID).
     * Performs a JOIN to fetch data from both Product and Accessory tables.
     *
     * @param id The ID of the Accessory to retrieve.
     * @return The Accessory object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Accessory getById(Integer id) throws SQLException {
        if (id == null || id == 0) {
            return null;
        }

        String sql = "SELECT p.ProductId, p.SKU, p.ProductName, p.BasePrice, p.CurrentPrice, p.StockQuantity, p.ProductType, p.CreatedAt, p.LastUpdated, p.IsActive, " +
                "a.AccessoryID, a.AccessoryType, a.Material, a.Color, a.Dimensions, a.Compatibility " +
                "FROM Product p JOIN Accessory a ON p.ProductId = a.AccessoryID WHERE p.ProductId = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractAccessoryFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all Accessories from the database, optionally ordered by a specified column.
     * Performs a JOIN to fetch data from both Product and Accessory tables.
     *
     * @param order The column name to order the results by (e.g., "ProductName", "AccessoryType").
     * @return A collection of Accessory objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<Accessory> getAll(String order) throws SQLException {
        List<Accessory> accessories = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.ProductId, p.SKU, p.ProductName, p.BasePrice, p.CurrentPrice, p.StockQuantity, p.ProductType, p.CreatedAt, p.LastUpdated, p.IsActive, " +
                        "a.AccessoryID, a.AccessoryType, a.Material, a.Color, a.Dimensions, a.Compatibility " +
                        "FROM Product p JOIN Accessory a ON p.ProductId = a.AccessoryID " +
                        "WHERE p.ProductType = 'Accessory'" // Ensure we only get Accessory products
        );

        String actualOrderColumn = DEFAULT_ORDER_COLUMN;
        if (order != null && !order.trim().isEmpty() && ALLOWED_ORDER_COLUMNS.contains(order)) {
            actualOrderColumn = order; // Use the provided order column
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accessories.add(extractAccessoryFromResultSet(rs));
            }
        }
        return accessories;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return ALLOWED_ORDER_COLUMNS;
    }

    /**
     * Helper method to extract a complete Accessory object from a ResultSet,
     * joining data from both Product and Accessory tables.
     *
     * @param rs The ResultSet containing joined product and accessory data.
     * @return A populated Accessory object.
     * @throws SQLException if a database access error occurs.
     */
    private Accessory extractAccessoryFromResultSet(ResultSet rs) throws SQLException {
        Accessory accessory = new Accessory();
        // Product fields
        accessory.setProductId(rs.getInt("ProductId"));
        accessory.setSku(rs.getString("SKU"));
        accessory.setProductName(rs.getString("ProductName"));
        accessory.setBasePrice(rs.getDouble("BasePrice"));
        accessory.setCurrentPrice(rs.getDouble("CurrentPrice"));
        accessory.setStockQuantity(rs.getInt("StockQuantity"));
        accessory.setProductType(Product.ProductType.valueOf(rs.getString("ProductType")));

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            accessory.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        Timestamp lastUpdatedTimestamp = rs.getTimestamp("LastUpdated");
        if (lastUpdatedTimestamp != null) {
            accessory.setLastUpdated(lastUpdatedTimestamp.toLocalDateTime());
        }
        accessory.setActive(rs.getBoolean("IsActive"));

        // Accessory specific fields
        String accessoryTypeStr = rs.getString("AccessoryType");
        if (accessoryTypeStr != null) {
            accessory.setAccessoryType(AccessoryType.valueOf(accessoryTypeStr));
        }

        accessory.setMaterial(rs.getString("Material"));
        accessory.setColor(rs.getString("Color"));
        accessory.setDimensions(rs.getString("Dimensions"));
        accessory.setCompatibility(rs.getString("Compatibility"));

        return accessory;
    }

    /**
     * Helper method to check if an Accessory specific entry exists for a given ProductID,
     * without fetching product details. Used internally for save logic's update path.
     */
    private Accessory getByIdInternal(int accessoryId, Connection connection) throws SQLException {
        String sql = "SELECT AccessoryID FROM Accessory WHERE AccessoryID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accessoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Accessory acc = new Accessory();
                    acc.setProductId(rs.getInt("AccessoryID"));
                    return acc;
                }
            }
        }
        return null;
    }

    // Helper methods to set nullable PreparedStatement parameters
    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value != null && !value.trim().isEmpty()) {
            ps.setString(index, value);
        } else {
            ps.setNull(index, java.sql.Types.VARCHAR);
        }
    }

    private void validateAccessory(Accessory accessory) {
        if (accessory == null) {
            throw new IllegalArgumentException("Accessory cannot be null.");
        }
        // Product validations (replicated from ProductDAO)
        if (accessory.getSku() == null || accessory.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty.");
        }
        if (accessory.getProductName() == null || accessory.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product Name cannot be null or empty.");
        }
        if (accessory.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base Price must be positive.");
        }
        if (accessory.getCurrentPrice() <= 0) {
            throw new IllegalArgumentException("Current Price must be positive.");
        }
        if (accessory.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock Quantity cannot be negative.");
        }
        // Ensure ProductType is set to Accessory
        if (accessory.getProductType() != Product.ProductType.Accessory) {
            System.err.println("Warning: Accessory's ProductType was not 'Accessory'. Setting it to 'Accessory'.");
            accessory.setProductType(Product.ProductType.Accessory);
        }
        // CreatedAt will be handled in save if null for new products

        // Accessory specific validations
        if (accessory.getAccessoryType() == null) {
            throw new IllegalArgumentException("Accessory Type cannot be null.");
        }
    }
}