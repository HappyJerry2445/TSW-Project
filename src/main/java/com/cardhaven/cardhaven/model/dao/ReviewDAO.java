package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ReviewDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class ReviewDAO implements GenericDAO<ReviewDTO, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "RewiewID", "ProductID", "UserID", "Rating", "Title", "ReviewText", "CreatedAt", "RewiewStatus"
    );

    private static final String DEFAULT_ORDER_COLUMN = "RewiewID";

    private final DataSource dataSource;

    public ReviewDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(ReviewDTO reviewDTO) throws SQLException {
        if (reviewDTO == null ||
                reviewDTO.getProductId() == 0 || reviewDTO.getUserId() == 0 || reviewDTO.getTitle() == null
                || reviewDTO.getTitle().trim().isEmpty() || reviewDTO.getReviewText() == null || reviewDTO.getReviewText().trim().isEmpty() ||
                reviewDTO.getReviewStatus() == null) {
            throw new IllegalArgumentException("Review object cannot be null.");
        }

        String sql;
        if (reviewDTO.getReviewId() == 0) {
            sql = "INSERT INTO Review (ProductID, UserID, Rating, Title, ReviewText, CreatedAt,ReviewStatus) VALUES (?,?,?,?,?,?,?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, reviewDTO.getProductId());
                ps.setInt(2, reviewDTO.getUserId());
                ps.setInt(3, reviewDTO.getRating());
                ps.setString(4, reviewDTO.getTitle());
                ps.setString(5, reviewDTO.getReviewText());
                ps.setTimestamp(6, (reviewDTO.getCreatedAt() != null) ? Timestamp.valueOf(reviewDTO.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, reviewDTO.getReviewStatus().name());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert row into Review table, no rows affected.");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reviewDTO.setReviewId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no rows affected");
                    }
                }
            }
        } else {
            sql = "UPDATE Review SET ProductID = ?, UserID = ?, Rating = ?,Title = ?, ReviewText = ?, CreatedAt = ?, ReviewStatus = ? WHERE ReviewID = ? ";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, reviewDTO.getProductId());
                ps.setInt(2, reviewDTO.getUserId());
                ps.setInt(3, reviewDTO.getRating());
                ps.setString(4, reviewDTO.getTitle());
                ps.setString(5, reviewDTO.getReviewText());
                ps.setTimestamp(6, (reviewDTO.getCreatedAt() != null) ? Timestamp.valueOf(reviewDTO.getCreatedAt()) : null);
                ps.setString(7, reviewDTO.getReviewStatus().name());
                ps.setInt(8, reviewDTO.getReviewId());

                ps.executeUpdate();
            }

        }
    }

    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("Review cannot be null or zero on delete");
        }

        String sql = "DELETE FROM Review WHERE ReviewID = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }

    }

    public ReviewDTO getById(Integer reviewID) throws SQLException {
        if (reviewID == null || reviewID <= 0) {
            throw new IllegalArgumentException("Review ID cannot be null or zero or greater than zero");
        }
        String sql = "SELECT ReviewID, ProductID, UserID, Rating, Title, ReviewText, CreatedAt, ReviewStatus FROM Review WHERE ReviewID = ?";
        ReviewDTO reviewDTO = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reviewDTO = extractReviewFromResultSet(rs);
                }
            }
        }
        return reviewDTO;
    }

    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    public Collection<ReviewDTO> getAll(String order) throws SQLException {
        Collection<ReviewDTO> reviewDTOs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ReviewID, ProductID, UserID, Rating, Title, ReviewText, CreatedAt, ReviewStatus FROM Review");

        String actualOrderColumn = DEFAULT_ORDER_COLUMN;
        if (order != null) {
            String timmedOrder = order.trim();
            if (ALLOWED_ORDER_COLUMNS.contains(timmedOrder)) {
                actualOrderColumn = timmedOrder;
            } else {
                System.err.println("Warning: Attempted to order by invalid column: '" + order + "'. Falling back to default order.");
            }
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                reviewDTOs.add(extractReviewFromResultSet(rs));
            }
        }
        return reviewDTOs;
    }

    private ReviewDTO extractReviewFromResultSet(ResultSet rs) throws SQLException {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setReviewId(rs.getInt("ReviewID"));
        reviewDTO.setProductId(rs.getInt("ProductID"));
        reviewDTO.setUserId(rs.getInt("UserID"));
        reviewDTO.setRating(rs.getInt("Rating"));
        reviewDTO.setTitle(rs.getString("Title"));
        reviewDTO.setReviewText(rs.getString("ReviewText"));

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            reviewDTO.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        reviewDTO.setReviewStatus(ReviewDTO.ReviewStatus.valueOf(rs.getString("ReviewStatus")));
        return reviewDTO;
    }

    /**
     * Retrieves a filtered collection of reviews from the database.
     *
     * @param order        The column name to order the results by.
     *                     Pass null or an empty string for default order.
     * @param desc         Descending or not
     * @param productId    Optional. Filters reviews by product ID.
     * @param userId       Optional. Filters reviews by user ID.
     * @param reviewStatus Optional. Filters reviews by status.
     * @param minRating    Optional. Filters reviews with rating >= this value.
     * @param maxRating    Optional. Filters reviews with rating <= this value.
     * @return A collection of Review objects.
     * @throws SQLException if a database access error occurs.
     */
    public Collection<ReviewDTO> getFilteredReviews(String order, boolean desc, Integer productId, Integer userId, ReviewDTO.ReviewStatus reviewStatus, Integer minRating, Integer maxRating) throws SQLException {
        Collection<ReviewDTO> reviewDTOs = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ReviewID, ProductID, UserID, Rating, Title, ReviewText, CreatedAt, ReviewStatus FROM Review WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (productId != null && productId > 0) {
            sql.append(" AND ProductID = ?");
            params.add(productId);
        }
        if (userId != null && userId > 0) {
            sql.append(" AND UserID = ?");
            params.add(userId);
        }
        if (reviewStatus != null) {
            sql.append(" AND ReviewStatus = ?");
            params.add(reviewStatus.name());
        }
        if (minRating != null && minRating >= 1 && minRating <= 5) {
            sql.append(" AND Rating >= ?");
            params.add(minRating);
        }
        if (maxRating != null && maxRating >= 1 && maxRating <= 5) {
            sql.append(" AND Rating <= ?");
            params.add(maxRating);
        }


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
        if (desc) {
            sql.append(" DESC");
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reviewDTOs.add(extractReviewFromResultSet(rs));
                }
            }
        }
        return reviewDTOs;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM `Review`";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }


}
