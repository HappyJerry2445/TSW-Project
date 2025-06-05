package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.OrderDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class OrderDAO implements GenericDAO<OrderDTO, Integer> {

	private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
			"OrderID", "UserID", "OrderDate", "OrderStatus", "ShippingAddressID", "BillingAddressID", "TotalAmount"
	);

	private static final String DEFAULT_ORDER_COLUMN= "OrderID";

	private final DataSource dataSource;

	public OrderDAO(DataSource dataSource) {
		this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
	}

	public void save(OrderDTO orderDTO) throws SQLException {
		if(orderDTO == null || orderDTO.getUserId() == 0 ||
			orderDTO.getOrderStatus() == null || orderDTO.getShippingAddressId() == 0 ||
			orderDTO.getBillingAddressId() == 0 || orderDTO.getTotalAmount() == 0) {
			throw new SQLException("UserID, OrderStatus, ShippingAddressID, BillingAddressID, TotalAmount cannot be 0/null/empty");
		}

		String sql;
		if(orderDTO.getOrderId()==0){
			sql="INSERT INTO Order (UserID, OrderDate, OrderStatus, ShippingAddressID, BillingAddressID, TotalAmount) VALUES (?,?,?,?,?,?)";
			try(Connection conn = dataSource.getConnection();
				 PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
				ps.setInt(1, orderDTO.getUserId());
				ps.setTimestamp(2, (orderDTO.getOrderDate() != null) ? Timestamp.valueOf(orderDTO.getOrderDate()) : Timestamp.valueOf(LocalDateTime.now()));
				ps.setString(3, orderDTO.getOrderStatus().name());
				ps.setInt(4, orderDTO.getShippingAddressId());
				ps.setInt(5, orderDTO.getBillingAddressId());
				ps.setInt(6, orderDTO.getTotalAmount());

				int affectedRows = ps.executeUpdate();
				if(affectedRows == 0){
					throw new SQLException("Creating order failed, 0 rows affected.");
				}

				try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
					if(generatedKeys.next()){
						orderDTO.setOrderId(generatedKeys.getInt(1));
					}else{
						throw new SQLException("Creating order failed, no ID obtained.");
					}
				}
			}
		}else{
			sql="UPDATE Order SET UserID = ?, OrderDATE = ?, OrderStatus = ?, ShippingAddressID = ?, BillingAddressID = ?, TotalAmount = ?  WHERE OrderID = ?";
			try(Connection connection = dataSource.getConnection();
			 	 PreparedStatement ps = connection.prepareStatement(sql)){
				ps.setInt(1, orderDTO.getUserId());
				ps.setTimestamp(2, (orderDTO.getOrderDate() != null) ? Timestamp.valueOf(orderDTO.getOrderDate()) : null);
				ps.setString(3, orderDTO.getOrderStatus().name());
				ps.setInt(4, orderDTO.getShippingAddressId());
				ps.setInt(5, orderDTO.getBillingAddressId());
				ps.setInt(6, orderDTO.getTotalAmount());
				ps.setInt(7, orderDTO.getOrderId());

				ps.executeUpdate();
			}
		}
	}

	public boolean delete(Integer orderId) throws SQLException {
		if(orderId == null || orderId == 0) {
			throw new SQLException("UserID, OrderID cannot be 0");
		}

		String sql="DELETE FROM Order WHERE OrderID = ?";
		try(Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, orderId);
			int affectedRows = ps.executeUpdate();
			return affectedRows > 0;
		}
	}

	@Override
	public OrderDTO getById(Integer orderID) throws SQLException {
		if(orderID == null || orderID <= 0) {
			throw new SQLException("UserID, OrderID cannot be 0 or less");
		}

		String sql="SELECT OrderID, UserID, OrderStatus, ShippingAddressID, BillingAddressID, TotalAmount FROM Order WHERE OrderID = ?";
		OrderDTO orderDTO = null;
		try(Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, orderID);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					orderDTO = extractOrderFromResultSet(rs);
				}
			}
		}
		return orderDTO;
	}

	public Collection<OrderDTO> getAll(String order) throws SQLException{
		Collection<OrderDTO> orderDTOS = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM Order ");

		String actualOrderColumn= DEFAULT_ORDER_COLUMN;
		if(order != null && !order.isEmpty()) {
			String trimmedOrder = order.trim();
			if(ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
				actualOrderColumn = trimmedOrder;
			} else {
				System.err.println("Warning: Attempted to order by invalid column: '" + order + "'. Falling back to default order.");
			}
		}
		sql.append(" ORDER BY ").append(actualOrderColumn);

		try(Connection conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql.toString());
			ResultSet rs = ps.executeQuery()){
				while(rs.next()){
					orderDTOS.add(extractOrderFromResultSet(rs));
				}
			}
			return orderDTOS;

	}

	public List<String> getAllowedOrderColumns() {
		return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
	}

	public List<OrderDTO> getOrderByUserID(int userID) throws SQLException {
		if(userID <=0){
			throw new SQLException("UserID, OrderID cannot be 0 or less");
		}

		List<OrderDTO> orderDTOS = new ArrayList<>();
		String sql = "SELECT OrderID, UserID, OrderStatus, ShippingAddressID, BillingAddressID, TotalAmount FROM `Order` WHERE UserID = ?";
		try(Connection conn = dataSource.getConnection();
			 PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setInt(1, userID);
			try(ResultSet rs = ps.executeQuery()){
				while(rs.next()){
					orderDTOS.add(extractOrderFromResultSet(rs));
				}
			}
		}
		return orderDTOS;
	}



	private OrderDTO extractOrderFromResultSet(ResultSet rs) throws SQLException {
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(rs.getInt("OrderID"));
		orderDTO.setUserId(rs.getInt("UserID"));
		orderDTO.setBillingAddressId(rs.getInt("BillingAddressID"));
		orderDTO.setShippingAddressId(rs.getInt("ShippingAddressID"));
		orderDTO.setOrderStatus(OrderDTO.OrderStatus.valueOf(rs.getString("OrderStatus")));

		Timestamp orderDate = rs.getTimestamp("OrderDate");
		if(orderDate != null) {
			orderDTO.setOrderDate(orderDate.toLocalDateTime());
		}
		return orderDTO;
	}
}
