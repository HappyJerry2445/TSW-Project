package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.OrderAddressDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderAddressDAO implements GenericDAO<OrderAddressDTO, Integer> {
    private static final String TABLE_NAME = "OrderAddress";
    private final DataSource dataSource;

    public OrderAddressDAO(DataSource dataSource) {
        this.dataSource = dataSource;

    }

    /**
     * Saves a new order address snapshot to the database.
     * After saving, the method updates the passed OrderAddress object with the generated ID.
     * This is critical for linking it to the Order.
     *
     * @param address The OrderAddress object to save. It must not have an ID set.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public synchronized void save(OrderAddressDTO address) throws SQLException {
        String sql = "INSERT INTO " + TABLE_NAME + " (StreetAddress, City, State, PostalCode, Country) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, address.getStreetAddress());
            ps.setString(2, address.getCity());
            ps.setString(3, address.getState());
            ps.setString(4, address.getPostalCode());
            ps.setString(5, address.getCountry());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating address snapshot failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    address.setOrderAddressID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating address snapshot failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Deleting a snapshot is not a standard operation, but is implemented for completeness.
     * In a real system, you would likely prevent deletion if it's linked to an order.
     */
    @Override
    public synchronized boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE OrderAddressID = ?";
        int affectedRows = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            affectedRows = ps.executeUpdate();
        }
        return affectedRows > 0;
    }

    @Override
    public synchronized OrderAddressDTO getById(Integer id) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE OrderAddressID = ?";
        OrderAddressDTO address = null;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    address = new OrderAddressDTO();
                    address.setOrderAddressID(rs.getInt("OrderAddressID"));
                    address.setStreetAddress(rs.getString("StreetAddress"));
                    address.setCity(rs.getString("City"));
                    address.setState(rs.getString("State"));
                    address.setPostalCode(rs.getString("PostalCode"));
                    address.setCountry(rs.getString("Country"));
                }
            }
        }
        return address;
    }

    /**
     * Getting all address snapshots is not a typical use case but is implemented.
     */
    @Override
    public synchronized Collection<OrderAddressDTO> getAll(String order) throws SQLException {
        String sql = "SELECT * FROM " + TABLE_NAME;

        if (order != null && !order.isEmpty() && getAllowedOrderColumns().contains(order.split(" ")[0])) {
            sql += " ORDER BY " + order;
        }

        Collection<OrderAddressDTO> addresses = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OrderAddressDTO address = new OrderAddressDTO();
                address.setOrderAddressID(rs.getInt("OrderAddressID"));
                address.setStreetAddress(rs.getString("StreetAddress"));
                address.setCity(rs.getString("City"));
                address.setState(rs.getString("State"));
                address.setPostalCode(rs.getString("PostalCode"));
                address.setCountry(rs.getString("Country"));
                addresses.add(address);
            }
        }
        return addresses;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return List.of("OrderAddressID", "City", "Country");
    }

}
