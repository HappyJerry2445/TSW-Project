// AccessoryDAO.java
package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Accessory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AccessoryDAO implements GenericDAO<Accessory, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "AccessoryID", "AccessoryType", "Material", "Color", "Dimensions", "Compatibility"
    );
    private static final String DEFAULT_ORDER_COLUMN = "AccessoryID";

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
     * This method will attempt to INSERT if the AccessoryID does not exist, or UPDATE if it does.
     * Note: The `AccessoryID` for an Accessory should correspond to a `ProductID` from the `Product` table.
     * It's assumed that a `Product` entry has already been created and its ID is set in the `Accessory` object
     * before calling this save method for a new accessory.
     *
     * @param accessory The Accessory object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if critical accessory fields are null or empty/invalid.
     */
    @Override
    public void save(Accessory accessory) throws SQLException {
        if (accessory == null || accessory.getAccessoryId() == 0 ||
                accessory.getAccessoryType() == null || accessory.getAccessoryType().trim().isEmpty()) {
            throw new IllegalArgumentException("Accessory, AccessoryID (must be set), or AccessoryType cannot be null or empty/zero.");
        }

        // Check if the accessory already exists to decide between INSERT and UPDATE
        boolean exists = getById(accessory.getAccessoryId()) != null;

        String sql;
        if (!exists) { // New accessory (INSERT)
            sql = "INSERT INTO Accessory (AccessoryID, AccessoryType, Material, Color, Dimensions, Compatibility) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, accessory.getAccessoryId()); // AccessoryID is the PK, must be set
                ps.setString(2, accessory.getAccessoryType());
                // Handle nullable Material
                if (accessory.getMaterial() != null && !accessory.getMaterial().trim().isEmpty()) {
                    ps.setString(3, accessory.getMaterial());
                } else {
                    ps.setNull(3, java.sql.Types.VARCHAR);
                }
                // Handle nullable Color
                if (accessory.getColor() != null && !accessory.getColor().trim().isEmpty()) {
                    ps.setString(4, accessory.getColor());
                } else {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                }
                // Handle nullable Dimensions
                if (accessory.getDimensions() != null && !accessory.getDimensions().trim().isEmpty()) {
                    ps.setString(5, accessory.getDimensions());
                } else {
                    ps.setNull(5, java.sql.Types.VARCHAR);
                }
                // Handle nullable Compatibility
                if (accessory.getCompatibility() != null && !accessory.getCompatibility().trim().isEmpty()) {
                    ps.setString(6, accessory.getCompatibility());
                } else {
                    ps.setNull(6, java.sql.Types.VARCHAR);
                }

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating accessory failed, no rows affected. Ensure Product with AccessoryID exists.");
                }
            }
        } else { // Existing accessory (UPDATE)
            sql = "UPDATE Accessory SET AccessoryType = ?, Material = ?, Color = ?, Dimensions = ?, Compatibility = ? WHERE AccessoryID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, accessory.getAccessoryType());
                // Handle nullable Material
                if (accessory.getMaterial() != null && !accessory.getMaterial().trim().isEmpty()) {
                    ps.setString(2, accessory.getMaterial());
                } else {
                    ps.setNull(2, java.sql.Types.VARCHAR);
                }
                // Handle nullable Color
                if (accessory.getColor() != null && !accessory.getColor().trim().isEmpty()) {
                    ps.setString(3, accessory.getColor());
                } else {
                    ps.setNull(3, java.sql.Types.VARCHAR);
                }
                // Handle nullable Dimensions
                if (accessory.getDimensions() != null && !accessory.getDimensions().trim().isEmpty()) {
                    ps.setString(4, accessory.getDimensions());
                } else {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                }
                // Handle nullable Compatibility
                if (accessory.getCompatibility() != null && !accessory.getCompatibility().trim().isEmpty()) {
                    ps.setString(5, accessory.getCompatibility());
                } else {
                    ps.setNull(5, java.sql.Types.VARCHAR);
                }
                ps.setInt(6, accessory.getAccessoryId());

                ps.executeUpdate();
            }
        }
    }

    /**
     * Deletes an Accessory from the database.
     * Note: Deleting an Accessory by its AccessoryID will also implicitly mean the associated Product
     * might need to be handled, depending on the foreign key constraints and application logic.
     *
     * @param id The ID of the accessory to delete.
     * @return true if the accessory was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the accessory ID is null or zero.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("AccessoryID cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM Accessory WHERE AccessoryID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves an Accessory from the database by its primary key (AccessoryID).
     *
     * @param accessoryId The ID of the accessory to retrieve.
     * @return The Accessory object if found, or null if not found.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the accessory ID is null or not positive.
     */
    @Override
    public Accessory getById(Integer accessoryId) throws SQLException {
        if (accessoryId == null || accessoryId <= 0) {
            throw new IllegalArgumentException("AccessoryID must be a positive integer.");
        }

        String sql = "SELECT AccessoryID, AccessoryType, Material, Color, Dimensions, Compatibility FROM Accessory WHERE AccessoryID = ?";
        Accessory accessory = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, accessoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    accessory = extractAccessoryFromResultSet(rs);
                }
            }
        }
        return accessory;
    }

    /**
     * Retrieves all accessories from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by. Pass null or an empty string for no specific order.
     * @return A collection of Accessory objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<Accessory> getAll(String order) throws SQLException {
        Collection<Accessory> accessories = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT AccessoryID, AccessoryType, Material, Color, Dimensions, Compatibility FROM Accessory");
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

        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    /**
     * Helper method to extract an Accessory object from a ResultSet.
     *
     * @param rs The ResultSet containing accessory data.
     * @return A populated Accessory object.
     * @throws SQLException if a database access error occurs.
     */
    private Accessory extractAccessoryFromResultSet(ResultSet rs) throws SQLException {
        Accessory accessory = new Accessory();
        accessory.setAccessoryId(rs.getInt("AccessoryID"));
        accessory.setAccessoryType(rs.getString("AccessoryType"));
        accessory.setMaterial(rs.getString("Material")); // This can be null
        accessory.setColor(rs.getString("Color")); // This can be null
        accessory.setDimensions(rs.getString("Dimensions")); // This can be null
        accessory.setCompatibility(rs.getString("Compatibility")); // This can be null
        return accessory;
    }
}