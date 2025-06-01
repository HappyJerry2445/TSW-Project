// TradingCardDAO.java
package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.TradingCard;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TradingCardDAO implements GenericDAO<TradingCard, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "CardID", "CardSet", "CardNumber", "Rarity", "CardCondition", "Artist", "YearPublished"
    );
    private static final String DEFAULT_ORDER_COLUMN = "CardID";

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
     * This method will attempt to INSERT if the CardID does not exist, or UPDATE if it does.
     * Note: The `CardID` for a TradingCard should correspond to a `ProductID` from the `Product` table.
     * It's assumed that a `Product` entry has already been created and its ID is set in the `TradingCard` object
     * before calling this save method for a new trading card.
     *
     * @param tradingCard The TradingCard object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if critical trading card fields are null or empty/invalid.
     */
    @Override
    public void save(TradingCard tradingCard) throws SQLException {
        if (tradingCard == null || tradingCard.getCardId() == 0 ||
                tradingCard.getCardSet() == null || tradingCard.getCardSet().trim().isEmpty() ||
                tradingCard.getCardNumber() == null || tradingCard.getCardNumber().trim().isEmpty() ||
                tradingCard.getRarity() == null || tradingCard.getRarity().trim().isEmpty() ||
                tradingCard.getCardCondition() == null || tradingCard.getCardCondition().trim().isEmpty()) {
            throw new IllegalArgumentException("TradingCard, CardID (must be set), CardSet, CardNumber, Rarity, or CardCondition cannot be null or empty/zero.");
        }

        // Check if the trading card already exists to decide between INSERT and UPDATE
        boolean exists = getById(tradingCard.getCardId()) != null;

        String sql;
        if (!exists) { // New trading card (INSERT)
            sql = "INSERT INTO TradingCard (CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, tradingCard.getCardId()); // CardID is the PK, must be set
                ps.setString(2, tradingCard.getCardSet());
                ps.setString(3, tradingCard.getCardNumber());
                ps.setString(4, tradingCard.getRarity());
                ps.setString(5, tradingCard.getCardCondition());
                // Handle nullable CardText
                if (tradingCard.getCardText() != null && !tradingCard.getCardText().trim().isEmpty()) {
                    ps.setString(6, tradingCard.getCardText());
                } else {
                    ps.setNull(6, java.sql.Types.LONGVARCHAR);
                }
                // Handle nullable Artist
                if (tradingCard.getArtist() != null && !tradingCard.getArtist().trim().isEmpty()) {
                    ps.setString(7, tradingCard.getArtist());
                } else {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                }
                // Handle nullable YearPublished
                if (tradingCard.getYearPublished() != null) {
                    ps.setInt(8, tradingCard.getYearPublished());
                } else {
                    ps.setNull(8, java.sql.Types.INTEGER);
                }

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating trading card failed, no rows affected. Ensure Product with CardID exists.");
                }
            }
        } else { // Existing trading card (UPDATE)
            sql = "UPDATE TradingCard SET CardSet = ?, CardNumber = ?, Rarity = ?, CardCondition = ?, CardText = ?, Artist = ?, YearPublished = ? WHERE CardID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, tradingCard.getCardSet());
                ps.setString(2, tradingCard.getCardNumber());
                ps.setString(3, tradingCard.getRarity());
                ps.setString(4, tradingCard.getCardCondition());
                // Handle nullable CardText
                if (tradingCard.getCardText() != null && !tradingCard.getCardText().trim().isEmpty()) {
                    ps.setString(5, tradingCard.getCardText());
                } else {
                    ps.setNull(5, java.sql.Types.LONGVARCHAR);
                }
                // Handle nullable Artist
                if (tradingCard.getArtist() != null && !tradingCard.getArtist().trim().isEmpty()) {
                    ps.setString(6, tradingCard.getArtist());
                } else {
                    ps.setNull(6, java.sql.Types.VARCHAR);
                }
                // Handle nullable YearPublished
                if (tradingCard.getYearPublished() != null) {
                    ps.setInt(7, tradingCard.getYearPublished());
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }
                ps.setInt(8, tradingCard.getCardId());

                ps.executeUpdate();
            }
        }
    }

    /**
     * Deletes a TradingCard from the database.
     * Note: Deleting a TradingCard by its CardID will also implicitly mean the associated Product
     * might need to be handled, depending on the foreign key constraints and application logic.
     *
     * @param id The ID of the trading card to delete.
     * @return true if the trading card was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the trading card ID is null or zero.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("CardID cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM TradingCard WHERE CardID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a TradingCard from the database by its primary key (CardID).
     *
     * @param cardId The ID of the trading card to retrieve.
     * @return The TradingCard object if found, or null if not found.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the card ID is null or not positive.
     */
    @Override
    public TradingCard getById(Integer cardId) throws SQLException {
        if (cardId == null || cardId <= 0) {
            throw new IllegalArgumentException("CardID must be a positive integer.");
        }

        String sql = "SELECT CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished FROM TradingCard WHERE CardID = ?";
        TradingCard tradingCard = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tradingCard = extractTradingCardFromResultSet(rs);
                }
            }
        }
        return tradingCard;
    }

    /**
     * Retrieves all trading cards from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by. Pass null or an empty string for no specific order.
     * @return A collection of TradingCard objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<TradingCard> getAll(String order) throws SQLException {
        Collection<TradingCard> tradingCards = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished FROM TradingCard");
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
                tradingCards.add(extractTradingCardFromResultSet(rs));
            }
        }
        return tradingCards;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    /**
     * Helper method to extract a TradingCard object from a ResultSet.
     *
     * @param rs The ResultSet containing trading card data.
     * @return A populated TradingCard object.
     * @throws SQLException if a database access error occurs.
     */
    private TradingCard extractTradingCardFromResultSet(ResultSet rs) throws SQLException {
        TradingCard tradingCard = new TradingCard();
        tradingCard.setCardId(rs.getInt("CardID"));
        tradingCard.setCardSet(rs.getString("CardSet"));
        tradingCard.setCardNumber(rs.getString("CardNumber"));
        tradingCard.setRarity(rs.getString("Rarity"));
        tradingCard.setCardCondition(rs.getString("CardCondition"));
        tradingCard.setCardText(rs.getString("CardText")); // This can be null
        tradingCard.setArtist(rs.getString("Artist")); // This can be null

        // Handle nullable YearPublished
        int yearPublished = rs.getInt("YearPublished");
        if (rs.wasNull()) {
            tradingCard.setYearPublished(null);
        } else {
            tradingCard.setYearPublished(yearPublished);
        }
        return tradingCard;
    }
}