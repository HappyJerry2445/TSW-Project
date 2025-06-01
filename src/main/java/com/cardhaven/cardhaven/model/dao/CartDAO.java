package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.CartDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class CartDAO implements GenericDAO<CartDTO, Integer> {

    // Columns in the Cart table: CartID, UserID, CreatedAt, LastUpdated
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "CartID", "UserID", "CreatedAt", "LastUpdated"
    );
    private static final String DEFAULT_ORDER_COLUMN = "CartID";

    private final DataSource dataSource;

    /**
     * Constructor for CartDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public CartDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves a Cart bean to the database.
     * If the CartID is 0, it performs an INSERT operation.
     * Otherwise, it performs an UPDATE operation based on the CartID.
     *
     * @param cartDTO The Cart object to save.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void save(CartDTO cartDTO) throws SQLException {
        if (cartDTO == null) {
            throw new IllegalArgumentException("Cart cannot be null.");
        }
        if (cartDTO.getUserId() == 0) {
            throw new IllegalArgumentException("UserID cannot be 0 for a cart.");
        }

        String sql;
        // Set CreatedAt for new carts and LastUpdated for all saves
        LocalDateTime now = LocalDateTime.now();
        if (cartDTO.getCartId() == 0) { // New cart (INSERT)
            cartDTO.setCreatedAt(now);
            cartDTO.setLastUpdated(now);
            sql = "INSERT INTO Cart (UserID, CreatedAt, LastUpdated) VALUES (?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cartDTO.getUserId());
                ps.setTimestamp(2, Timestamp.valueOf(cartDTO.getCreatedAt()));
                ps.setTimestamp(3, Timestamp.valueOf(cartDTO.getLastUpdated()));

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating cart failed, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cartDTO.setCartId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating cart failed, no ID obtained.");
                    }
                }
            }
        } else { // Existing cart (UPDATE)
            cartDTO.setLastUpdated(now);
            sql = "UPDATE Cart SET LastUpdated = ? WHERE CartID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(cartDTO.getLastUpdated()));
                ps.setInt(2, cartDTO.getCartId());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    // This could mean the cart with the given ID was not found
                    throw new SQLException("Updating cart failed, no rows affected. CartID " + cartDTO.getCartId() + " may not exist.");
                }
            }
        }
    }

    /**
     * Deletes a Cart from the database.
     *
     * @param cartId The ID of the cart to delete.
     * @return true if the cart was deleted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean delete(Integer cartId) throws SQLException {
        if (cartId == null || cartId <= 0) {
            throw new IllegalArgumentException("CartID must be a positive integer for deletion.");
        }

        String sql = "DELETE FROM Cart WHERE CartID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a Cart from the database by its primary key (CartID).
     *
     * @param cartId The ID of the cart to retrieve.
     * @return The Cart object if found, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public CartDTO getById(Integer cartId) throws SQLException {
        if (cartId == null || cartId <= 0) {
            throw new IllegalArgumentException("CartID must be a positive integer.");
        }

        String sql = "SELECT CartID, UserID, CreatedAt, LastUpdated FROM Cart WHERE CartID = ?";
        CartDTO cartDTO = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cartDTO = extractCartFromResultSet(rs);
                }
            }
        }
        return cartDTO;
    }

    /**
     * Retrieves a Cart from the database by UserID.
     * Assumes a user can only have one cart (due to UNIQUE (UserID) constraint in schema).
     *
     * @param userId The ID of the user whose cart is to be retrieved.
     * @return The Cart object if found, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    public CartDTO getByUserId(Integer userId) throws SQLException {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("UserID must be a positive integer.");
        }

        String sql = "SELECT CartID, UserID, CreatedAt, LastUpdated FROM Cart WHERE UserID = ?";
        CartDTO cartDTO = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cartDTO = extractCartFromResultSet(rs);
                }
            }
        }
        return cartDTO;
    }


    /**
     * Retrieves all carts from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by.
     *              Pass null or an empty string for default order.
     * @return A collection of Cart objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<CartDTO> getAll(String order) throws SQLException {
        Collection<CartDTO> cartDTOS = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT CartID, UserID, CreatedAt, LastUpdated FROM Cart");

        String actualOrderColumn = DEFAULT_ORDER_COLUMN; // Default ordering
        if (order != null && !order.trim().isEmpty()) {
            String trimmedOrder = order.trim();
            if (ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
                actualOrderColumn = trimmedOrder;
            } else {
                System.err.println("Warning: Attempted to order Carts by invalid column: '" + order + "'. Falling back to default order: " + DEFAULT_ORDER_COLUMN);
                // Optionally, throw an IllegalArgumentException here if strict validation is preferred
                // throw new IllegalArgumentException("Invalid order column: " + order);
            }
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cartDTOS.add(extractCartFromResultSet(rs));
            }
        }
        return cartDTOS;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS); // Return a copy
    }

    /**
     * Helper method to extract a Cart object from a ResultSet.
     *
     * @param rs The ResultSet containing cart data.
     * @return A populated Cart object.
     * @throws SQLException if a database access error occurs.
     */
    private CartDTO extractCartFromResultSet(ResultSet rs) throws SQLException {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(rs.getInt("CartID"));
        cartDTO.setUserId(rs.getInt("UserID"));
        cartDTO.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        cartDTO.setLastUpdated(rs.getTimestamp("LastUpdated").toLocalDateTime());
        return cartDTO;
    }
}