package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Product.ProductType;
import com.cardhaven.cardhaven.model.beans.TradingCard;
import com.cardhaven.cardhaven.model.beans.TradingCard.CardCondition;
import com.cardhaven.cardhaven.model.beans.TradingCard.Rarity;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TradingCardDAO implements GenericDAO<TradingCard, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductId", "SKU", "ProductName", "BasePrice", "CurrentPrice", "StockQuantity",
            "CardID", "CardSet", "CardNumber", "Rarity", "CardCondition", "Artist", "YearPublished",
            "CreatedAt", "LastUpdated", "IsActive"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductId";

    private final DataSource dataSource;

    /**
     * Constructor for TradingCardDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public TradingCardDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves a TradingCard bean to the database.
     * If the ProductId is 0, it performs an INSERT operation (first to Product, then to TradingCard).
     * Otherwise, it performs an UPDATE operation (first to Product, then to TradingCard).
     * This method manages its own transaction.
     *
     * @param tradingCard The TradingCard object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if tradingCard or essential fields are null/empty.
     */
    @Override
    public void save(TradingCard tradingCard) throws SQLException {
        validateTradingCard(tradingCard);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction for atomicity

            // 1. Save the Product part (INSERT or UPDATE)
            String productSql;
            if (tradingCard.getProductId() == 0) { // New Product (and TradingCard)
                productSql = "INSERT INTO Product (SKU, ProductName, BasePrice, CurrentPrice, StockQuantity, ProductType, CreatedAt, LastUpdated, IsActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(productSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, tradingCard.getSku());
                    ps.setString(2, tradingCard.getProductName());
                    ps.setDouble(3, tradingCard.getBasePrice());
                    ps.setDouble(4, tradingCard.getCurrentPrice());
                    ps.setDouble(5, tradingCard.getStockQuantity());
                    ps.setString(6, tradingCard.getProductType().name());
                    ps.setTimestamp(7, (tradingCard.getCreatedAt() != null) ? Timestamp.valueOf(tradingCard.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(8, (tradingCard.getLastUpdated() != null) ? Timestamp.valueOf(tradingCard.getLastUpdated()) : null);
                    ps.setBoolean(9, tradingCard.isActive());

                    int affectedRows = ps.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Creating product failed, no rows affected.");
                    }

                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            tradingCard.setProductId(generatedKeys.getInt(1)); // Set the generated Product ID
                        } else {
                            throw new SQLException("Creating product failed, no ID obtained.");
                        }
                    }
                }
            } else { // Existing Product (UPDATE)
                productSql = "UPDATE Product SET SKU = ?, ProductName = ?, BasePrice = ?, CurrentPrice = ?, StockQuantity = ?, ProductType = ?, LastUpdated = ?, IsActive = ? WHERE ProductId = ?";
                try (PreparedStatement ps = connection.prepareStatement(productSql)) {
                    ps.setString(1, tradingCard.getSku());
                    ps.setString(2, tradingCard.getProductName());
                    ps.setDouble(3, tradingCard.getBasePrice());
                    ps.setDouble(4, tradingCard.getCurrentPrice());
                    ps.setDouble(5, tradingCard.getStockQuantity());
                    ps.setString(6, tradingCard.getProductType().name());
                    ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // Update LastUpdated
                    ps.setBoolean(8, tradingCard.isActive());
                    ps.setInt(9, tradingCard.getProductId());
                    ps.executeUpdate();
                }
            }

            // 2. Save the TradingCard specific part (INSERT or UPDATE)
            String tradingCardSql;
            if (getByIdInternal(tradingCard.getProductId(), connection) == null) { // Check if TradingCard entry exists for this ProductId
                tradingCardSql = "INSERT INTO TradingCard (CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(tradingCardSql)) {
                    ps.setInt(1, tradingCard.getProductId()); // Use the Product ID as CardID
                    ps.setString(2, tradingCard.getCardSet());
                    ps.setString(3, tradingCard.getCardNumber());
                    ps.setString(4, tradingCard.getRarity().name());
                    setNullableString(ps, 5, (tradingCard.getCardCondition() != null) ? tradingCard.getCardCondition().name() : null);
                    setNullableString(ps, 6, tradingCard.getCardText());
                    setNullableString(ps, 7, tradingCard.getArtist());
                    setNullableInteger(ps, 8, tradingCard.getYearPublished());
                    ps.executeUpdate();
                }
            } else { // Existing TradingCard (UPDATE)
                tradingCardSql = "UPDATE TradingCard SET CardSet = ?, CardNumber = ?, Rarity = ?, CardCondition = ?, CardText = ?, Artist = ?, YearPublished = ? WHERE CardID = ?";
                try (PreparedStatement ps = connection.prepareStatement(tradingCardSql)) {
                    ps.setString(1, tradingCard.getCardSet());
                    ps.setString(2, tradingCard.getCardNumber());
                    ps.setString(3, tradingCard.getRarity().name());
                    setNullableString(ps, 4, (tradingCard.getCardCondition() != null) ? tradingCard.getCardCondition().name() : null);
                    setNullableString(ps, 5, tradingCard.getCardText());
                    setNullableString(ps, 6, tradingCard.getArtist());
                    setNullableInteger(ps, 7, tradingCard.getYearPublished());
                    ps.setInt(8, tradingCard.getProductId()); // Use the Product ID as CardID for WHERE clause
                    ps.executeUpdate();
                }
            }
            connection.commit(); // Commit transaction if all successful
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Rollback on error
            }
            System.err.println("Error saving TradingCard: " + e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close(); // Close connection
            }
        }
    }

    /**
     * Deletes a TradingCard from the database by its ID (which is also ProductID).
     * This method manages its own transaction and explicitly deletes from both tables.
     *
     * @param id The ID (which is also ProductID) of the TradingCard to delete.
     * @return true if the TradingCard was deleted, false otherwise.
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

            // Delete from TradingCard table first
            String deleteTradingCardSql = "DELETE FROM TradingCard WHERE CardID = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteTradingCardSql)) {
                ps.setInt(1, id);
                ps.executeUpdate(); // Execute, but don't check affected rows yet, as Product delete is next
            }

            // Then delete from Product table
            String deleteProductSql = "DELETE FROM Product WHERE ProductId = ?";
            try (PreparedStatement ps = connection.prepareStatement(deleteProductSql)) {
                ps.setInt(1, id);
                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    // If product wasn't found, then TradingCard might not have been either,
                    // or there was a problem with the Product ID.
                    // This indicates an issue but might not be a full rollback scenario if TradingCard was already gone.
                    // For robustness, consider if the TradingCard delete should return true if only one table is affected.
                    // For now, we'll indicate success only if Product is deleted (assuming TradingCard was too).
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
            System.err.println("Error deleting TradingCard with ID " + id + ": " + e.getMessage());
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close(); // Close connection
            }
        }
    }

    /**
     * Retrieves a TradingCard by its ProductID (which is also the CardID).
     * Performs a JOIN to fetch data from both Product and TradingCard tables.
     *
     * @param id The ID of the TradingCard to retrieve.
     * @return The TradingCard object if found, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public TradingCard getById(Integer id) throws SQLException {
        if (id == null || id == 0) {
            return null;
        }

        String sql = "SELECT p.ProductId, p.SKU, p.ProductName, p.BasePrice, p.CurrentPrice, p.StockQuantity, p.ProductType, p.CreatedAt, p.LastUpdated, p.IsActive, " +
                "tc.CardID, tc.CardSet, tc.CardNumber, tc.Rarity, tc.CardCondition, tc.CardText, tc.Artist, tc.YearPublished " +
                "FROM Product p JOIN TradingCard tc ON p.ProductId = tc.CardID WHERE p.ProductId = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractTradingCardFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all TradingCards from the database, optionally ordered by a specified column.
     * Performs a JOIN to fetch data from both Product and TradingCard tables.
     *
     * @param order The column name to order the results by (e.g., "ProductName", "Rarity").
     * @return A collection of TradingCard objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<TradingCard> getAll(String order) throws SQLException {
        List<TradingCard> tradingCards = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT p.ProductId, p.SKU, p.ProductName, p.BasePrice, p.CurrentPrice, p.StockQuantity, p.ProductType, p.CreatedAt, p.LastUpdated, p.IsActive, " +
                        "tc.CardID, tc.CardSet, tc.CardNumber, tc.Rarity, tc.CardCondition, tc.CardText, tc.Artist, tc.YearPublished " +
                        "FROM Product p JOIN TradingCard tc ON p.ProductId = tc.CardID"
        );

        String actualOrderColumn = DEFAULT_ORDER_COLUMN;
        if (order != null && !order.trim().isEmpty() && ALLOWED_ORDER_COLUMNS.contains(order)) {
            actualOrderColumn = order;
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tradingCards.add(extractTradingCardFromResultSet(rs));
            }
        }
        return tradingCards;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return ALLOWED_ORDER_COLUMNS;
    }

    /**
     * Helper method to extract a complete TradingCard object from a ResultSet,
     * joining data from both Product and TradingCard tables.
     *
     * @param rs The ResultSet containing joined product and trading card data.
     * @return A populated TradingCard object.
     * @throws SQLException if a database access error occurs.
     */
    private TradingCard extractTradingCardFromResultSet(ResultSet rs) throws SQLException {
        TradingCard tradingCard = new TradingCard();
        // Product fields
        tradingCard.setProductId(rs.getInt("ProductId"));
        tradingCard.setSku(rs.getString("SKU"));
        tradingCard.setProductName(rs.getString("ProductName"));
        tradingCard.setBasePrice(rs.getDouble("BasePrice"));
        tradingCard.setCurrentPrice(rs.getDouble("CurrentPrice"));
        tradingCard.setStockQuantity(rs.getInt("StockQuantity"));
        tradingCard.setProductType(ProductType.valueOf(rs.getString("ProductType"))); // Convert string to enum
        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            tradingCard.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        Timestamp lastUpdatedTimestamp = rs.getTimestamp("LastUpdated");
        if (lastUpdatedTimestamp != null) {
            tradingCard.setLastUpdated(lastUpdatedTimestamp.toLocalDateTime());
        }
        tradingCard.setActive(rs.getBoolean("IsActive"));

        // TradingCard specific fields
        // CardID is same as ProductId, so no need to set again if already set from ProductId
        tradingCard.setCardSet(rs.getString("CardSet"));
        tradingCard.setCardNumber(rs.getString("CardNumber"));

        String rarityStr = rs.getString("Rarity");
        if (rarityStr != null) {
            tradingCard.setRarity(Rarity.valueOf(rarityStr));
        }

        String conditionStr = rs.getString("CardCondition");
        if (conditionStr != null) {
            tradingCard.setCardCondition(CardCondition.valueOf(conditionStr));
        } else {
            tradingCard.setCardCondition(null); // Explicitly set to null if DB value is NULL
        }

        tradingCard.setCardText(rs.getString("CardText"));
        tradingCard.setArtist(rs.getString("Artist"));

        Integer yearPublishedObj = rs.getObject("YearPublished", Integer.class); // Use getObject for nullable Integer
        tradingCard.setYearPublished(yearPublishedObj);

        return tradingCard;
    }


    /**
     * Helper method to check if a TradingCard specific entry exists for a given ProductID,
     * without fetching product details. Used internally for save logic's update path.
     */
    private TradingCard getByIdInternal(int cardId, Connection connection) throws SQLException {
        String sql = "SELECT CardID FROM TradingCard WHERE CardID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Only need to know if it exists, so return a minimal object or just true/false
                    TradingCard tc = new TradingCard();
                    tc.setProductId(rs.getInt("CardID"));
                    return tc;
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

    private void setNullableInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value != null) {
            ps.setInt(index, value);
        } else {
            ps.setNull(index, java.sql.Types.INTEGER);
        }
    }

    private void validateTradingCard(TradingCard tradingCard) {
        if (tradingCard == null) {
            throw new IllegalArgumentException("TradingCard cannot be null.");
        }
        // Product validations (replicated from ProductDAO)
        if (tradingCard.getSku() == null || tradingCard.getSku().trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be null or empty.");
        }
        if (tradingCard.getProductName() == null || tradingCard.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product Name cannot be null or empty.");
        }
        if (tradingCard.getBasePrice() <= 0) {
            throw new IllegalArgumentException("Base Price must be positive.");
        }
        if (tradingCard.getCurrentPrice() <= 0) {
            throw new IllegalArgumentException("Current Price must be positive.");
        }
        if (tradingCard.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock Quantity cannot be negative.");
        }
        if (tradingCard.getProductType() == null) {
            throw new IllegalArgumentException("Product Type cannot be null.");
        }
        if (tradingCard.getCreatedAt() == null && tradingCard.getProductId() == 0) {
            // For new products, CreatedAt should not be null. For existing, it's read from DB.
            // If it's 0 (new) and null, we'll set it to now in save, so this check might be too strict.
            // Let's allow null for new, and set it in the DAO.
        }

        // TradingCard specific validations
        if (tradingCard.getCardSet() == null || tradingCard.getCardSet().trim().isEmpty()) {
            throw new IllegalArgumentException("Card Set cannot be null or empty.");
        }
        if (tradingCard.getCardNumber() == null || tradingCard.getCardNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Card Number cannot be null or empty.");
        }
        if (tradingCard.getRarity() == null) {
            throw new IllegalArgumentException("Rarity cannot be null.");
        }
    }
}