package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Product;
import com.cardhaven.cardhaven.model.beans.TradingCard;
import com.cardhaven.cardhaven.model.beans.TradingCard.CardCondition;
import com.cardhaven.cardhaven.model.beans.TradingCard.Rarity;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TradingCardDAO implements GenericDAO<TradingCard, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "CardID", "CardSet", "CardNumber", "Rarity", "CardCondition", "Artist", "YearPublished"
    );
    private static final String DEFAULT_ORDER_COLUMN = "CardID";

    private final DataSource dataSource;
    private final ProductDAO productDAO;

    /**
     * Constructor for TradingCardDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     * @param productDAO The ProductDAO instance to handle common product operations.
     */
    public TradingCardDAO(DataSource dataSource, ProductDAO productDAO) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
        this.productDAO = Objects.requireNonNull(productDAO, "ProductDAO cannot be null for TradingCardDAO.");
    }

    /**
     * Saves a TradingCard bean to the database.
     * If the ID is 0, it performs an INSERT operation (first to Product, then to TradingCard).
     * Otherwise, it performs an UPDATE operation (first to Product, then to TradingCard).
     *
     * @param tradingCard The TradingCard object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if tradingCard or essential fields are null/empty.
     */
    @Override
    public void save(TradingCard tradingCard) throws SQLException {
        if (tradingCard == null || tradingCard.getSku() == null || tradingCard.getSku().trim().isEmpty() ||
                tradingCard.getProductName() == null || tradingCard.getProductName().trim().isEmpty() ||
                tradingCard.getBasePrice() <= 0 || tradingCard.getCurrentPrice() <= 0 ||
                tradingCard.getStockQuantity() < 0 ||
                tradingCard.getProductType() == null || tradingCard.getCardSet() == null ||
                tradingCard.getCardSet().trim().isEmpty() || tradingCard.getCardNumber() == null ||
                tradingCard.getCardNumber().trim().isEmpty() || tradingCard.getRarity() == null) {
            throw new IllegalArgumentException("TradingCard and its essential fields (sku, name, prices, stock, category, productType, cardSet, cardNumber, rarity) cannot be null or invalid.");
        }

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // 1. Save the Product part of the TradingCard
            productDAO.save(tradingCard); // This will insert or update the Product table and set the productID (id)

            String sql;
            if (tradingCard.getProductId() == 0) { // Should not happen if productDAO.save worked for a new product
                throw new SQLException("Product ID was not generated after saving product.");
            }

            // 2. Save the TradingCard specific part
            if (getByIdNoProductFetch(tradingCard.getProductId(), connection) == null) { // Check if TradingCard entry exists
                sql = "INSERT INTO TradingCard (CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, tradingCard.getProductId()); // Use the generated Product ID as CardID
                    ps.setString(2, tradingCard.getCardSet());
                    ps.setString(3, tradingCard.getCardNumber());
                    ps.setString(4, tradingCard.getRarity().name()); // Store enum as string
                    setNullableString(ps, 5, (tradingCard.getCardCondition() != null) ? tradingCard.getCardCondition().name() : null); // Handle nullable enum
                    setNullableString(ps, 6, tradingCard.getCardText());
                    setNullableString(ps, 7, tradingCard.getArtist());
                    setNullableInteger(ps, 8, tradingCard.getYearPublished());

                    ps.executeUpdate();
                }
            } else { // Existing TradingCard (UPDATE)
                sql = "UPDATE TradingCard SET CardSet = ?, CardNumber = ?, Rarity = ?, CardCondition = ?, CardText = ?, Artist = ?, YearPublished = ? WHERE CardID = ?";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback(); // Rollback on error
            }
            System.err.println("Error saving TradingCard: " + e.getMessage());
            throw e; // Re-throw the exception
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit
                connection.close();
            }
        }
    }

    /**
     * Deletes a TradingCard from the database by its ID.
     * This will cascade delete from the Product table due to foreign key constraints.
     *
     * @param id The ID (which is also ProductID) of the TradingCard to delete.
     * @return true if the TradingCard was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is null or zero.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("TradingCard ID cannot be null or zero for deletion.");
        }

        // Deleting from the Product table will cascade delete from TradingCard table
        return productDAO.delete(id);
    }

    /**
     * Retrieves a TradingCard from the database by its ID (which is also ProductID).
     *
     * @param id The ID of the TradingCard to retrieve.
     * @return The TradingCard object if found, or null if not found.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is not positive.
     */
    @Override
    public TradingCard getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("TradingCard ID must be a positive integer.");
        }

        // 1. Get the base Product information
        Product product = productDAO.getById(id);
        if (product == null) {
            return null; // No product found with this ID
        }

        // 2. Get the TradingCard specific information
        String sql = "SELECT CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished FROM TradingCard WHERE CardID = ?";
        TradingCard tradingCard = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tradingCard = extractTradingCardSpecificsFromResultSet(rs);
                    // Populate inherited Product fields
                    tradingCard.setProductId(product.getProductId());
                    tradingCard.setSku(product.getSku());
                    tradingCard.setProductName(product.getProductName());
                    tradingCard.setBasePrice(product.getBasePrice());
                    tradingCard.setCurrentPrice(product.getCurrentPrice());
                    tradingCard.setStockQuantity(product.getStockQuantity());
                    tradingCard.setProductType(product.getProductType());
                    tradingCard.setCreatedAt(product.getCreatedAt());
                    tradingCard.setLastUpdated(product.getLastUpdated());
                    tradingCard.setActive(product.isActive());
                }
            }
        }
        return tradingCard;
    }

    /**
     * Retrieves all TradingCards from the database, optionally ordered by a specified column.
     * This method performs a JOIN to get both Product and TradingCard details.
     *
     * @param order The column name to order the results by.
     * @return A collection of TradingCard objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<TradingCard> getAll(String order) throws SQLException {
        Collection<TradingCard> tradingCards = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.ProductID, p.SKU, p.ProductName, p.BasePrice, p.CurrentPrice, p.StockQuantity, p.ProductType, p.CreatedAt, p.LastUpdated, p.IsActive, ")
                .append("tc.CardID, tc.CardSet, tc.CardNumber, tc.Rarity, tc.CardCondition, tc.CardText, tc.Artist, tc.YearPublished ")
                .append("FROM Product p JOIN TradingCard tc ON p.ProductID = tc.CardID ")
                .append("WHERE p.ProductType = 'TradingCard'"); // Ensure we only get TradingCard products

        String actualOrderColumn = DEFAULT_ORDER_COLUMN;
        if (order != null && !order.trim().isEmpty()) {
            String trimmedOrder = order.trim();
            // Check if ordering by a TradingCard specific column or a Product column
            if (ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
                actualOrderColumn = "tc." + trimmedOrder; // Prefix with table alias
            } else if (productDAO.getAllowedOrderColumns().contains(trimmedOrder)) { // Assuming ProductDAO has this helper
                actualOrderColumn = "p." + trimmedOrder; // Prefix with table alias
            } else {
                System.err.println("Warning: Attempted to order by invalid column: '" + order + "'. Falling back to default order.");
            }
        } else {
            actualOrderColumn = "tc." + DEFAULT_ORDER_COLUMN;
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);


        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tradingCards.add(extractFullTradingCardFromResultSet(rs));
            }
        }
        return tradingCards;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return ALLOWED_ORDER_COLUMNS;
    }

    /**
     * Helper method to extract only TradingCard specific attributes from a ResultSet.
     * Used when fetching only the TradingCard table data, not the joined Product data.
     *
     * @param rs The ResultSet containing TradingCard data.
     * @return A populated TradingCard object with only its specific fields set.
     * @throws SQLException if a database access error occurs.
     */
    private TradingCard extractTradingCardSpecificsFromResultSet(ResultSet rs) throws SQLException {
        TradingCard tradingCard = new TradingCard();
        tradingCard.setCardSet(rs.getString("CardSet"));
        tradingCard.setCardNumber(rs.getString("CardNumber"));
        tradingCard.setRarity(Rarity.valueOf(rs.getString("Rarity")));

        String cardConditionStr = rs.getString("CardCondition");
        if (cardConditionStr != null && !rs.wasNull()) {
            tradingCard.setCardCondition(CardCondition.valueOf(cardConditionStr));
        } else {
            tradingCard.setCardCondition(null);
        }

        String cardText = rs.getString("CardText");
        if (rs.wasNull()) {
            tradingCard.setCardText(null);
        } else {
            tradingCard.setCardText(cardText);
        }

        String artist = rs.getString("Artist");
        if (rs.wasNull()) {
            tradingCard.setArtist(null);
        } else {
            tradingCard.setArtist(artist);
        }

        int yearPublished = rs.getInt("YearPublished");
        if (rs.wasNull()) {
            tradingCard.setYearPublished(null);
        } else {
            tradingCard.setYearPublished(yearPublished);
        }
        return tradingCard;
    }

    /**
     * Helper method to extract a full TradingCard object (including Product fields) from a joined ResultSet.
     *
     * @param rs The ResultSet containing joined Product and TradingCard data.
     * @return A populated TradingCard object.
     * @throws SQLException if a database access error occurs.
     */
    private TradingCard extractFullTradingCardFromResultSet(ResultSet rs) throws SQLException {
        // First, extract Product fields (similar logic to ProductDAO's extract method)
        int productID = rs.getInt("ProductID");
        String sku = rs.getString("SKU");
        String productName = rs.getString("ProductName");
        double basePrice = rs.getDouble("BasePrice");
        double currentPrice = rs.getDouble("CurrentPrice");
        int stockQuantity = rs.getInt("StockQuantity");
        Product.ProductType productType = Product.ProductType.valueOf(rs.getString("ProductType"));
        LocalDateTime createdAt = rs.getTimestamp("CreatedAt").toLocalDateTime();
        Timestamp lastUpdatedTimestamp = rs.getTimestamp("LastUpdated");
        LocalDateTime lastUpdated = (lastUpdatedTimestamp != null) ? lastUpdatedTimestamp.toLocalDateTime() : null;
        boolean isActive = rs.getBoolean("IsActive");

        // Then, extract TradingCard specific fields
        String cardSet = rs.getString("CardSet");
        String cardNumber = rs.getString("CardNumber");
        Rarity rarity = Rarity.valueOf(rs.getString("Rarity"));

        String cardConditionStr = rs.getString("CardCondition");
        CardCondition cardCondition = (cardConditionStr != null && !rs.wasNull()) ? CardCondition.valueOf(cardConditionStr) : null;

        String cardText = rs.getString("CardText");
        if (rs.wasNull()) cardText = null;

        String artist = rs.getString("Artist");
        if (rs.wasNull()) artist = null;

        int yearPublished = rs.getInt("YearPublished");
        Integer yearPublishedObj = rs.wasNull() ? null : yearPublished;

        // Create the TradingCard object using the constructor that takes all fields
        return new TradingCard(
                productID, sku, productName, basePrice, currentPrice, stockQuantity,
                productType, createdAt, lastUpdated, isActive,
                cardSet, cardNumber, rarity, cardCondition, cardText, artist, yearPublishedObj
        );
    }

    /**
     * Helper method to check if a TradingCard specific entry exists for a given ProductID,
     * without fetching product details. Used internally for save logic.
     */
    private TradingCard getByIdNoProductFetch(int cardId, Connection connection) throws SQLException {
        String sql = "SELECT CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished FROM TradingCard WHERE CardID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractTradingCardSpecificsFromResultSet(rs);
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
}