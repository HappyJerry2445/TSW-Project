package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.OrderItemDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class OrderItemDAO implements GenericDAO<OrderItemDTO, Integer> {

	private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
			"OrderItemID", "OrderID", "ProductID", "VariantID", "Quantity", "UnitPrice"
	);
	private static final String DEFAULT_ORDER_COLUMN = "OrderItemID";

	private final DataSource dataSource;

	public OrderItemDAO(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
	}

	@Override
	public void save(OrderItemDTO orderItemDTO) throws SQLException {
		if (orderItemDTO == null || orderItemDTO.getOrderID() == 0 || orderItemDTO.getQuantity() <= 0 ||
				orderItemDTO.getUnitPrice() == null) {
			throw new IllegalArgumentException("OrderItem, OrderID, Quantity, or UnitPrice cannot be null or invalid.");
		}

		String sql;
		if (orderItemDTO.getOrderItemID() == 0) {
			sql = "INSERT INTO OrderItem (OrderID, ProductID, VariantID, Quantity, UnitPrice) VALUES (?, ?, ?, ?, ?)";
			try (Connection connection = dataSource.getConnection();
				 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, orderItemDTO.getOrderID());
				if (orderItemDTO.getProductID() != null) {
					ps.setInt(2, orderItemDTO.getProductID());
				} else {
					ps.setNull(2, java.sql.Types.INTEGER);
				}
				if (orderItemDTO.getVariantID() != null) {
					ps.setInt(3, orderItemDTO.getVariantID());
				} else {
					ps.setNull(3, java.sql.Types.INTEGER);
				}
				ps.setInt(4, orderItemDTO.getQuantity());
				ps.setBigDecimal(5, orderItemDTO.getUnitPrice());

				int affectedRows = ps.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Creating order item failed, no rows affected.");
				}

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						orderItemDTO.setOrderItemID(generatedKeys.getInt(1));
					} else {
						throw new SQLException("Creating order item failed, no ID obtained.");
					}
				}
			}
		} else {
			sql = "UPDATE OrderItem SET OrderID = ?, ProductID = ?, VariantID = ?, Quantity = ?, UnitPrice = ? WHERE OrderItemID = ?";
			try (Connection connection = dataSource.getConnection();
				 PreparedStatement ps = connection.prepareStatement(sql)) {
				ps.setInt(1, orderItemDTO.getOrderID());
				if (orderItemDTO.getProductID() != null) {
					ps.setInt(2, orderItemDTO.getProductID());
				} else {
					ps.setNull(2, java.sql.Types.INTEGER);
				}
				if (orderItemDTO.getVariantID() != null) {
					ps.setInt(3, orderItemDTO.getVariantID());
				} else {
					ps.setNull(3, java.sql.Types.INTEGER);
				}
				ps.setInt(4, orderItemDTO.getQuantity());
				ps.setBigDecimal(5, orderItemDTO.getUnitPrice());
				ps.setInt(6, orderItemDTO.getOrderItemID());

				ps.executeUpdate();
			}
		}
	}

	@Override
	public boolean delete(Integer id) throws SQLException {
		if (id == null || id == 0) {
			throw new IllegalArgumentException("OrderItemID cannot be null or zero for deletion.");
		}

		String sql = "DELETE FROM OrderItem WHERE OrderItemID = ?";
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, id);
			int affectedRows = ps.executeUpdate();
			return affectedRows > 0;
		}
	}

	@Override
	public OrderItemDTO getById(Integer orderItemID) throws SQLException {
		if (orderItemID == null || orderItemID <= 0) {
			throw new IllegalArgumentException("OrderItemID must be a positive integer.");
		}

		String sql = "SELECT OrderItemID, OrderID, ProductID, VariantID, Quantity, UnitPrice FROM OrderItem WHERE OrderItemID = ?";
		OrderItemDTO orderItemDTO = null;
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement ps = connection.prepareStatement(sql)) {
			ps.setInt(1, orderItemID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					orderItemDTO = extractOrderItemFromResultSet(rs);
				}
			}
		}
		return orderItemDTO;
	}

	@Override
	public Collection<OrderItemDTO> getAll(String order) throws SQLException {
		Collection<OrderItemDTO> orderItems = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT OrderItemID, OrderID, ProductID, VariantID, Quantity, UnitPrice, AddedAT FROM OrderItem");

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
				orderItems.add(extractOrderItemFromResultSet(rs));
			}
		}
		return orderItems;
	}

	@Override
	public List<String> getAllowedOrderColumns() {
		return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
	}

	private OrderItemDTO extractOrderItemFromResultSet(ResultSet rs) throws SQLException {
		OrderItemDTO orderItemDTO = new OrderItemDTO();
		orderItemDTO.setOrderItemID(rs.getInt("OrderItemID"));
		orderItemDTO.setOrderID(rs.getInt("OrderID"));

		int productID = rs.getInt("ProductID");
		if (rs.wasNull()) {
			orderItemDTO.setProductID(null);
		} else {
			orderItemDTO.setProductID(productID);
		}

		int variantID = rs.getInt("VariantID");
		if (rs.wasNull()) {
			orderItemDTO.setVariantID(null);
		} else {
			orderItemDTO.setVariantID(variantID);
		}

		orderItemDTO.setQuantity(rs.getInt("Quantity"));
		orderItemDTO.setUnitPrice(rs.getBigDecimal("UnitPrice"));

		return orderItemDTO;
	}
}