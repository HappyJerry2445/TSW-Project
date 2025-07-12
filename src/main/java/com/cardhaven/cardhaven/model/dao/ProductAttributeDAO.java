package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ProductAttributeDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class ProductAttributeDAO implements GenericDAO<ProductAttributeDTO, Map<String, Integer>> {


    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductID", "AttributeID", "Value"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductID";

    private final DataSource dataSource;

    public ProductAttributeDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }


    @Override
    public void save(ProductAttributeDTO productAttributeDTO) throws SQLException {
        if (productAttributeDTO == null || productAttributeDTO.getProductID() == 0 ||
                productAttributeDTO.getAttributeID() == 0 || productAttributeDTO.getValue() == null) {
            throw new IllegalArgumentException("ProductAttribute, ProductID, AttributeID, or Value cannot be null/empty or zero.");
        }

        // Try to insert first
        String insertSql = "INSERT INTO ProductAttribute (ProductID, AttributeID, Value) VALUES (?, ?, ?)";
        String updateSql = "UPDATE ProductAttribute SET Value = ? WHERE ProductID = ? AND AttributeID = ?";

        try (Connection connection = dataSource.getConnection()) {

            try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
                ps.setInt(1, productAttributeDTO.getProductID());
                ps.setInt(2, productAttributeDTO.getAttributeID());
                ps.setString(3, productAttributeDTO.getValue());
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {

                try (PreparedStatement ps = connection.prepareStatement(updateSql)) {
                    ps.setString(1, productAttributeDTO.getValue());
                    ps.setInt(2, productAttributeDTO.getProductID());
                    ps.setInt(3, productAttributeDTO.getAttributeID());
                    ps.executeUpdate();
                }
            }
        }
    }

    @Override
    public boolean delete(Map<String, Integer> ids) throws SQLException {
        if (ids == null || !ids.containsKey("productID") || !ids.containsKey("attributeID") ||
                ids.get("productID") == null || ids.get("productID") == 0 ||
                ids.get("attributeID") == null || ids.get("attributeID") == 0) {
            throw new IllegalArgumentException("ProductID and AttributeID must be provided for deletion.");
        }

        String sql = "DELETE FROM ProductAttribute WHERE ProductID = ? AND AttributeID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ids.get("productID"));
            ps.setInt(2, ids.get("attributeID"));
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public ProductAttributeDTO getById(Map<String, Integer> ids) throws SQLException {
        if (ids == null || !ids.containsKey("productID") || !ids.containsKey("attributeID") ||
                ids.get("productID") == null || ids.get("productID") <= 0 ||
                ids.get("attributeID") == null || ids.get("attributeID") <= 0) {
            throw new IllegalArgumentException("ProductID and AttributeID must be positive integers.");
        }

        String sql = "SELECT ProductID, AttributeID, Value FROM ProductAttribute WHERE ProductID = ? AND AttributeID = ?";
        ProductAttributeDTO productAttributeDTO = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ids.get("productID"));
            ps.setInt(2, ids.get("attributeID"));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productAttributeDTO = extractProductAttributeFromResultSet(rs);
                }
            }
        }
        return productAttributeDTO;
    }

    @Override
    public Collection<ProductAttributeDTO> getAll(String order) throws SQLException {
        Collection<ProductAttributeDTO> productAttributes = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ProductID, AttributeID, Value FROM ProductAttribute");

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
                productAttributes.add(extractProductAttributeFromResultSet(rs));
            }
        }
        return productAttributes;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    private ProductAttributeDTO extractProductAttributeFromResultSet(ResultSet rs) throws SQLException {
        ProductAttributeDTO productAttributeDTO = new ProductAttributeDTO();
        productAttributeDTO.setProductID(rs.getInt("ProductID"));
        productAttributeDTO.setAttributeID(rs.getInt("AttributeID"));
        productAttributeDTO.setValue(rs.getString("Value"));
        return productAttributeDTO;
    }
}