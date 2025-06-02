package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ReviewDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class ReviewDAO implements GenericDAO<ReviewDTO, Integer>{

	private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
			"RewiewID","ProductID", "UserID", "Rating","Title", "ReviewText", "CreatedAt", "RewiewStatus"
	);

	private static final String DEFAULT_ORDER_COLUMN ="RewiewID";

	private final DataSource dataSource;

	public ReviewDAO(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
	}

	public void save(ReviewDTO reviewDTO) throws SQLException {
		if (reviewDTO == null || reviewDTO.getReviewId() == 0 ||
				reviewDTO.getProductId() == 0 || reviewDTO.getUserId() == 0 || reviewDTO.getRating() == 0 || reviewDTO.getTitle() == null
				|| reviewDTO.getTitle().trim().isEmpty() || reviewDTO.getReviewText() == null || reviewDTO.getReviewText().trim().isEmpty() ||
				reviewDTO.getReviewStatus() == null) {
			throw new IllegalArgumentException("Review object cannot be null.");
		}

		String sql;
		if (reviewDTO.getReviewId() == 0) {
			sql = "INSERT INTO Review (ProductID, UserID, Rating, Title, ReviewText, CreatedAt,ReviewStatus) VALUES (?,?,?,?,?,?,?)";
			try (Connection conn = dataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
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
			sql = "UPDATE Review SET ProductID = ?, UserID = ?, Rating = ?,Title = ?, ReviewText = ?, CreatedAt = ?, RewiewStatus = ? WHERE ReviewID = ? ";
			try (Connection conn = dataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, reviewDTO.getProductId());
				ps.setInt(2, reviewDTO.getUserId());
				ps.setInt(3, reviewDTO.getRating());
				ps.setString(4, reviewDTO.getTitle());
				ps.setString(5, reviewDTO.getReviewText());
				ps.setTimestamp(6, (reviewDTO.getCreatedAt() != null) ? Timestamp.valueOf(reviewDTO.getCreatedAt()) : null);
				ps.setString(7, reviewDTO.getReviewStatus().name());

				ps.executeUpdate();
			}

		}
	}
		public boolean delete (Integer id) throws SQLException{
			if(id == null || id == 0) {
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
			if(reviewID == null || reviewID <= 0) {
				throw new IllegalArgumentException("Review ID cannot be null or zero or greater than zero");
			}
			String sql = "SELECT ReviewID, ProductID, UserID, Rating, Title, ReviewText, CreatedAt, ReviewStatus FROM Review WHERE ReviewID = ?";
			ReviewDTO reviewDTO = null;
			try (Connection conn = dataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(sql)) {
				ps.setInt(1, reviewID);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						reviewDTO = new ReviewDTO();
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
				ResultSet rs = ps.executeQuery()){
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

}
