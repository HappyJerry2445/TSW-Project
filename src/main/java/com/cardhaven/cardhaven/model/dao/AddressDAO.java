// AddressDAO.java
package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Address;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class AddressDAO implements GenericDAO<Address, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "AddressID", "UserID", "City", "Country", "AddressType", "IsDefault"
    );
    private static final String DEFAULT_ORDER_COLUMN = "AddressID";

    private final DataSource dataSource;

    /**
     * Constructor for AddressDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public AddressDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves an Address bean to the database.
     * If the AddressID is 0, it performs an INSERT operation.
     * Otherwise, it performs an UPDATE operation based on the AddressID.
     *
     * @param address The Address object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if address or essential fields are null/empty.
     */
    @Override
    public void save(Address address) throws SQLException {
        // Input validation: Ensure critical fields are not null or empty
        if (address == null || address.getUserID() == 0 ||
                address.getStreetAddress() == null || address.getStreetAddress().trim().isEmpty() ||
                address.getCity() == null || address.getCity().trim().isEmpty() ||
                address.getPostalCode() == null || address.getPostalCode().trim().isEmpty() ||
                address.getCountry() == null || address.getCountry().trim().isEmpty() ||
                address.getAddressType() == null) {
            throw new IllegalArgumentException("Address, UserID, StreetAddress, City, PostalCode, Country, or AddressType cannot be null/empty/zero.");
        }

        String sql;
        if (address.getAddressID() == 0) { // New address (INSERT)
            sql = "INSERT INTO Address (UserID, StreetAddress, City, State, PostalCode, Country, AddressType, IsDefault) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, address.getUserID());
                ps.setString(2, address.getStreetAddress());
                ps.setString(3, address.getCity());
                // Handle nullable State
                if (address.getState() != null && !address.getState().trim().isEmpty()) {
                    ps.setString(4, address.getState());
                } else {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                }
                ps.setString(5, address.getPostalCode());
                ps.setString(6, address.getCountry());
                ps.setString(7, address.getAddressType().name()); // Store enum as string
                ps.setBoolean(8, address.isDefault());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating address failed, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        address.setAddressID(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating address failed, no ID obtained.");
                    }
                }
            }
        } else { // Existing address (UPDATE)
            sql = "UPDATE Address SET UserID = ?, StreetAddress = ?, City = ?, State = ?, PostalCode = ?, Country = ?, AddressType = ?, IsDefault = ? WHERE AddressID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, address.getUserID());
                ps.setString(2, address.getStreetAddress());
                ps.setString(3, address.getCity());
                // Handle nullable State
                if (address.getState() != null && !address.getState().trim().isEmpty()) {
                    ps.setString(4, address.getState());
                } else {
                    ps.setNull(4, java.sql.Types.VARCHAR);
                }
                ps.setString(5, address.getPostalCode());
                ps.setString(6, address.getCountry());
                ps.setString(7, address.getAddressType().name());
                ps.setBoolean(8, address.isDefault());
                ps.setInt(9, address.getAddressID());

                ps.executeUpdate();
            }
        }
    }

    /**
     * Deletes an Address from the database.
     *
     * @param id The AddressID to delete.
     * @return true if the address was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is null or zero.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("AddressID cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM Address WHERE AddressID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves an Address from the database by its primary key (AddressID).
     *
     * @param addressID The ID of the address to retrieve.
     * @return The Address object if found, or null if not found.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is not positive.
     */
    @Override
    public Address getById(Integer addressID) throws SQLException {
        if (addressID == null || addressID <= 0) {
            throw new IllegalArgumentException("AddressID must be a positive integer.");
        }

        String sql = "SELECT AddressID, UserID, StreetAddress, City, State, PostalCode, Country, AddressType, IsDefault FROM Address WHERE AddressID = ?";
        Address address = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, addressID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    address = extractAddressFromResultSet(rs);
                }
            }
        }
        return address;
    }

    /**
     * Retrieves all addresses from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by (e.g., "City", "UserID").
     *              Pass null or an empty string for no specific order, or to use default.
     * @return A collection of Address objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<Address> getAll(String order) throws SQLException {
        Collection<Address> addresses = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT AddressID, UserID, StreetAddress, City, State, PostalCode, Country, AddressType, IsDefault FROM Address");

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
                addresses.add(extractAddressFromResultSet(rs));
            }
        }
        return addresses;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return ALLOWED_ORDER_COLUMNS;
    }

    /**
     * Retrieves all addresses for a specific user.
     *
     * @param userID The ID of the user whose addresses to retrieve.
     * @return A list of Address objects associated with the user.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the UserID is not positive.
     */
    public List<Address> getAddressesByUserId(int userID) throws SQLException {
        if (userID <= 0) {
            throw new IllegalArgumentException("UserID must be a positive integer.");
        }

        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT AddressID, UserID, StreetAddress, City, State, PostalCode, Country, AddressType, IsDefault FROM Address WHERE UserID = ? ORDER BY IsDefault DESC, AddressType";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    addresses.add(extractAddressFromResultSet(rs));
                }
            }
        }
        return addresses;
    }

    /**
     * Sets a specific address as default for a user for a given AddressType.
     * This method will first set all other addresses of the same type for the user to non-default.
     *
     * @param userID      The ID of the user.
     * @param addressID   The ID of the address to set as default.
     * @param addressType The type of address (Shipping or Billing) to set as default.
     * @return true if the default was successfully set, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if IDs are invalid or addressType is null.
     */
    public boolean setDefaultAddress(int userID, int addressID, Address.AddressType addressType) throws SQLException {
        if (userID <= 0 || addressID <= 0 || addressType == null) {
            throw new IllegalArgumentException("UserID, AddressID must be positive, and AddressType cannot be null.");
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Set all other addresses of the same type for this user to NOT default
            String updateOthersSql = "UPDATE Address SET IsDefault = FALSE WHERE UserID = ? AND AddressType = ? AND AddressID != ?";
            try (PreparedStatement psUpdateOthers = connection.prepareStatement(updateOthersSql)) {
                psUpdateOthers.setInt(1, userID);
                psUpdateOthers.setString(2, addressType.name());
                psUpdateOthers.setInt(3, addressID);
                psUpdateOthers.executeUpdate();
            }

            // 2. Set the specified address to default
            String updateTargetSql = "UPDATE Address SET IsDefault = TRUE WHERE AddressID = ? AND UserID = ? AND AddressType = ?";
            try (PreparedStatement psUpdateTarget = connection.prepareStatement(updateTargetSql)) {
                psUpdateTarget.setInt(1, addressID);
                psUpdateTarget.setInt(2, userID);
                psUpdateTarget.setString(3, addressType.name());
                int affectedRows = psUpdateTarget.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback(); // Rollback if target address not found or not updated
                    return false;
                }
            }

            connection.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Rollback on error
            }
            System.err.println("Error setting default address: " + e.getMessage());
            throw e; // Re-throw the exception after logging and rollback
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close();
            }
        }
    }


    /**
     * Helper method to extract an Address object from a ResultSet.
     *
     * @param rs The ResultSet containing address data.
     * @return A populated Address object.
     * @throws SQLException if a database access error occurs.
     */
    private Address extractAddressFromResultSet(ResultSet rs) throws SQLException {
        Address address = new Address();
        address.setAddressID(rs.getInt("AddressID"));
        address.setUserID(rs.getInt("UserID"));
        address.setStreetAddress(rs.getString("StreetAddress"));
        address.setCity(rs.getString("City"));

        // Handle nullable State
        String state = rs.getString("State");
        if (rs.wasNull()) {
            address.setState(null);
        } else {
            address.setState(state);
        }

        address.setPostalCode(rs.getString("PostalCode"));
        address.setCountry(rs.getString("Country"));
        address.setAddressType(Address.AddressType.valueOf(rs.getString("AddressType"))); // Convert string to enum
        address.setDefault(rs.getBoolean("IsDefault"));
        return address;
    }
}