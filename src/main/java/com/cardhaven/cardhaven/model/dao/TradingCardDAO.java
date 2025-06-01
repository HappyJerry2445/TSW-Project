// TradingCardDAO.java
package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.TradingCardDTO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TradingCardDAO implements GenericDAO<TradingCardDTO, Integer> {

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
     * @param tradingCardDTO The TradingCard object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if critical trading card fields are null or empty/invalid.
     */
    @Override
    public void save(TradingCardDTO tradingCardDTO) throws SQLException {
        if (tradingCardDTO == null || tradingCardDTO.getCardId() == 0 ||
                tradingCardDTO.getCardSet() == null || tradingCardDTO.getCardSet().trim().isEmpty() ||
                tradingCardDTO.getCardNumber() == null || tradingCardDTO.getCardNumber().trim().isEmpty() ||
                tradingCardDTO.getRarity() == null || tradingCardDTO.getRarity().trim().isEmpty() ||
                tradingCardDTO.getCardCondition() == null || tradingCardDTO.getCardCondition().trim().isEmpty()) {
            throw new IllegalArgumentException("TradingCard, CardID (must be set), CardSet, CardNumber, Rarity, or CardCondition cannot be null or empty/zero.");
        }

        // Check if the trading card already exists to decide between INSERT and UPDATE
        boolean exists = getById(tradingCardDTO.getCardId()) != null;

        String sql;
        if (!exists) { // New trading card (INSERT)
            sql = "INSERT INTO TradingCard (CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, tradingCardDTO.getCardId()); // CardID is the PK, must be set
                ps.setString(2, tradingCardDTO.getCardSet());
                ps.setString(3, tradingCardDTO.getCardNumber());
                ps.setString(4, tradingCardDTO.getRarity());
                ps.setString(5, tradingCardDTO.getCardCondition());
                // Handle nullable CardText
                if (tradingCardDTO.getCardText() != null && !tradingCardDTO.getCardText().trim().isEmpty()) {
                    ps.setString(6, tradingCardDTO.getCardText());
                } else {
                    ps.setNull(6, java.sql.Types.LONGVARCHAR);
                }
                // Handle nullable Artist
                if (tradingCardDTO.getArtist() != null && !tradingCardDTO.getArtist().trim().isEmpty()) {
                    ps.setString(7, tradingCardDTO.getArtist());
                } else {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                }
                // Handle nullable YearPublished
                if (tradingCardDTO.getYearPublished() != null) {
                    ps.setInt(8, tradingCardDTO.getYearPublished());
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
                ps.setString(1, tradingCardDTO.getCardSet());
                ps.setString(2, tradingCardDTO.getCardNumber());
                ps.setString(3, tradingCardDTO.getRarity());
                ps.setString(4, tradingCardDTO.getCardCondition());
                // Handle nullable CardText
                if (tradingCardDTO.getCardText() != null && !tradingCardDTO.getCardText().trim().isEmpty()) {
                    ps.setString(5, tradingCardDTO.getCardText());
                } else {
                    ps.setNull(5, java.sql.Types.LONGVARCHAR);
                }
                // Handle nullable Artist
                if (tradingCardDTO.getArtist() != null && !tradingCardDTO.getArtist().trim().isEmpty()) {
                    ps.setString(6, tradingCardDTO.getArtist());
                } else {
                    ps.setNull(6, java.sql.Types.VARCHAR);
                }
                // Handle nullable YearPublished
                if (tradingCardDTO.getYearPublished() != null) {
                    ps.setInt(7, tradingCardDTO.getYearPublished());
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }
                ps.setInt(8, tradingCardDTO.getCardId());

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
    public TradingCardDTO getById(Integer cardId) throws SQLException {
        if (cardId == null || cardId <= 0) {
            throw new IllegalArgumentException("CardID must be a positive integer.");
        }

        String sql = "SELECT CardID, CardSet, CardNumber, Rarity, CardCondition, CardText, Artist, YearPublished FROM TradingCard WHERE CardID = ?";
        TradingCardDTO tradingCardDTO = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, cardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tradingCardDTO = extractTradingCardFromResultSet(rs);
                }
            }
        }
        return tradingCardDTO;
    }

    /**
     * Retrieves all trading cards from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by. Pass null or an empty string for no specific order.
     * @return A collection of TradingCard objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<TradingCardDTO> getAll(String order) throws SQLException {
        Collection<TradingCardDTO> tradingCardDTOS = new ArrayList<>();
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
                tradingCardDTOS.add(extractTradingCardFromResultSet(rs));
            }
        }
        return tradingCardDTOS;
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
    private TradingCardDTO extractTradingCardFromResultSet(ResultSet rs) throws SQLException {
        TradingCardDTO tradingCardDTO = new TradingCardDTO();
        tradingCardDTO.setCardId(rs.getInt("CardID"));
        tradingCardDTO.setCardSet(rs.getString("CardSet"));
        tradingCardDTO.setCardNumber(rs.getString("CardNumber"));
        tradingCardDTO.setRarity(rs.getString("Rarity"));
        tradingCardDTO.setCardCondition(rs.getString("CardCondition"));
        tradingCardDTO.setCardText(rs.getString("CardText")); // This can be null
        tradingCardDTO.setArtist(rs.getString("Artist")); // This can be null

        // Handle nullable YearPublished
        int yearPublished = rs.getInt("YearPublished");
        if (rs.wasNull()) {
            tradingCardDTO.setYearPublished(null);
        } else {
            tradingCardDTO.setYearPublished(yearPublished);
        }
        return tradingCardDTO;
    }
}