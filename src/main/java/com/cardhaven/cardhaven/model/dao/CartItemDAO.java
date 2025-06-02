package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.CartItemDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class CartItemDAO implements GenericDAO<CartItemDTO, Integer> {

    // Columns in the CartItem table: CartItemID, CartID, ProductID, VartiantID, Quantity, AddedAT
    // Note: Schema has "VartiantID" and "AddedAT", DTO has "variantId" and "addedAt"
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "CartItemID", "CartID", "ProductID", "VartiantID", "Quantity", "AddedAT"
    );
    private static final String DEFAULT_ORDER_COLUMN = "CartItemID";

    private final DataSource dataSource;

    /**
     * Constructor for CartItemDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public CartItemDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves a CartItem bean to the database.
     * If the CartItemID is 0, it performs an INSERT operation.
     * Otherwise, it performs an UPDATE operation based on the CartItemID.
     *
     * @param cartItem The CartItem object to save.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void save(CartItemDTO cartItem) throws SQLException {
        if (cartItem == null) {
            throw new IllegalArgumentException("CartItem cannot be null.");
        }
        if (cartItem.getCartId() == 0 || cartItem.getProductId() == 0 || cartItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("CartID, ProductID must be positive, and Quantity must be greater than 0.");
        }

        String sql;
        // Set AddedAt for new cart items
        if (cartItem.getCartItemId() == 0) { // New cart item (INSERT)
            cartItem.setAddedAt(LocalDateTime.now());
            // Schema uses VartiantID and AddedAT
            sql = "INSERT INTO CartItem (CartID, ProductID, VartiantID, Quantity, AddedAT) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, cartItem.getCartId());
                ps.setInt(2, cartItem.getProductId());
                if (cartItem.getVariantId() != null && cartItem.getVariantId() != 0) {
                    ps.setInt(3, cartItem.getVariantId());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setInt(4, cartItem.getQuantity());
                ps.setTimestamp(5, Timestamp.valueOf(cartItem.getAddedAt()));

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating cart item failed, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cartItem.setCartItemId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating cart item failed, no ID obtained.");
                    }
                }
            }
        } else { // Existing cart item (UPDATE)
            // AddedAt is generally not updated for an existing item, but quantity or variant might change.
            // If AddedAt needs to be updated, set it before this block: cartItem.setAddedAt(LocalDateTime.now());
            // Schema uses VartiantID
            sql = "UPDATE CartItem SET CartID = ?, ProductID = ?, VartiantID = ?, Quantity = ? WHERE CartItemID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, cartItem.getCartId());
                ps.setInt(2, cartItem.getProductId());
                if (cartItem.getVariantId() != null && cartItem.getVariantId() != 0) {
                    ps.setInt(3, cartItem.getVariantId());
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setInt(4, cartItem.getQuantity());
                ps.setInt(5, cartItem.getCartItemId());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Updating cart item failed, no rows affected. CartItemID " + cartItem.getCartItemId() + " may not exist.");
                }
            }
        }
    }

    /**
     * Deletes a CartItem from the database.
     *
     * @param cartItemId The ID of the cart item to delete.
     * @return true if the cart item was deleted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean delete(Integer cartItemId) throws SQLException {
        if (cartItemId == null || cartItemId <= 0) {
            throw new IllegalArgumentException("CartItemID must be a positive integer for deletion.");
        }

        String sql = "DELETE FROM CartItem WHERE CartItemID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a CartItem from the database by its primary key (CartItemID).
     *
     * @param cartItemId The ID of the cart item to retrieve.
     * @return The CartItem object if found, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public CartItemDTO getById(Integer cartItemId) throws SQLException {
        if (cartItemId == null || cartItemId <= 0) {
            throw new IllegalArgumentException("CartItemID must be a positive integer.");
        }
        // Schema uses VartiantID and AddedAT
        String sql = "SELECT CartItemID, CartID, ProductID, VartiantID, Quantity, AddedAT FROM CartItem WHERE CartItemID = ?";
        CartItemDTO cartItem = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cartItem = extractCartItemFromResultSet(rs);
                }
            }
        }
        return cartItem;
    }

    /**
     * Retrieves all cart items for a specific cart.
     *
     * @param cartId The ID of the cart.
     * @param order  The column name to order the results by. Pass null or empty for default.
     * @return A collection of CartItem objects.
     * @throws SQLException if a database access error occurs.
     */
    public Collection<CartItemDTO> getByCartId(Integer cartId, String order) throws SQLException {
        if (cartId == null || cartId <= 0) {
            throw new IllegalArgumentException("CartID must be a positive integer.");
        }
        Collection<CartItemDTO> cartItems = new ArrayList<>();
        // Schema uses VartiantID and AddedAT
        StringBuilder sql = new StringBuilder("SELECT CartItemID, CartID, ProductID, VartiantID, Quantity, AddedAT FROM CartItem WHERE CartID = ?");

        String actualOrderColumn = DEFAULT_ORDER_COLUMN; // Default ordering for items within a cart
        if (order != null && !order.trim().isEmpty()) {
            String trimmedOrder = order.trim();
            if (ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
                actualOrderColumn = trimmedOrder;
            } else {
                System.err.println("Warning: Attempted to order CartItems by invalid column: '" + order + "'. Falling back to default order: " + DEFAULT_ORDER_COLUMN);
            }
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);


        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(extractCartItemFromResultSet(rs));
                }
            }
        }
        return cartItems;
    }


    /**
     * Retrieves all cart items from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by.
     *              Pass null or an empty string for default order.
     * @return A collection of CartItem objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<CartItemDTO> getAll(String order) throws SQLException {
        Collection<CartItemDTO> cartItems = new ArrayList<>();
        // Schema uses VartiantID and AddedAT
        StringBuilder sql = new StringBuilder("SELECT CartItemID, CartID, ProductID, VartiantID, Quantity, AddedAT FROM CartItem");

        String actualOrderColumn = DEFAULT_ORDER_COLUMN;
        if (order != null && !order.trim().isEmpty()) {
            String trimmedOrder = order.trim();
            if (ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
                actualOrderColumn = trimmedOrder;
            } else {
                System.err.println("Warning: Attempted to order CartItems by invalid column: '" + order + "'. Falling back to default order: " + DEFAULT_ORDER_COLUMN);
            }
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cartItems.add(extractCartItemFromResultSet(rs));
            }
        }
        return cartItems;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS); // Return a copy
    }

    /**
     * Helper method to extract a CartItem object from a ResultSet.
     *
     * @param rs The ResultSet containing cart item data.
     * @return A populated CartItem object.
     * @throws SQLException if a database access error occurs.
     */
    private CartItemDTO extractCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItemDTO item = new CartItemDTO();
        item.setCartItemId(rs.getInt("CartItemID"));
        item.setCartId(rs.getInt("CartID"));
        item.setProductId(rs.getInt("ProductID"));

        // Schema uses "VartiantID" (potential typo for VariantID)
        int variantId = rs.getInt("VartiantID");
        if (rs.wasNull()) {
            item.setVariantId(null);
        } else {
            item.setVariantId(variantId);
        }

        item.setQuantity(rs.getInt("Quantity"));
        // Schema uses "AddedAT" (potential typo for AddedAt)
        item.setAddedAt(rs.getTimestamp("AddedAT").toLocalDateTime());
        return item;
    }
}
