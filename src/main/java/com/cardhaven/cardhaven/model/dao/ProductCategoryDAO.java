// ProductCategoryDAO.java
package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.ProductCategory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProductCategoryDAO implements GenericDAO<ProductCategory, ProductCategory.ProductCategoryKey> {


    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductID", "CategoryID"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductID, CategoryID";

    private final DataSource dataSource;

    /**
     * Constructor for ProductCategoryDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public ProductCategoryDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves a ProductCategory bean to the database.
     * For composite keys like ProductCategory, this method will always attempt an INSERT.
     * If an entry with the same ProductID and CategoryID already exists, a SQLException
     * due to unique constraint violation will likely occur.
     *
     * @param productCategory The ProductCategory object to save.
     * @throws SQLException             if a database access error occurs (e.g., duplicate entry).
     * @throws IllegalArgumentException if critical product category fields are invalid.
     */
    @Override
    public void save(ProductCategory productCategory) throws SQLException {
        if (productCategory == null || productCategory.getProductId() == 0 || productCategory.getCategoryId() == 0) {
            throw new IllegalArgumentException("ProductID and CategoryID cannot be zero.");
        }

        String sql = "INSERT INTO ProductCategory (ProductID, CategoryID) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, productCategory.getProductId());
            ps.setInt(2, productCategory.getCategoryId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating product category association failed, no rows affected.");
            }
        }
    }

    /**
     * Deletes a ProductCategory association from the database by its composite primary key.
     *
     * @param id The ProductCategoryKey representing the composite ID (ProductID and CategoryID) to delete.
     * @return true if the association was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the composite ID is null or contains zero values.
     */
    @Override
    public boolean delete(ProductCategory.ProductCategoryKey id) throws SQLException {
        if (id == null || id.getProductId() == 0 || id.getCategoryId() == 0) {
            throw new IllegalArgumentException("ProductID and CategoryID in ProductCategoryKey cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM ProductCategory WHERE ProductID = ? AND CategoryID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id.getProductId());
            ps.setInt(2, id.getCategoryId());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a ProductCategory association from the database by its composite primary key.
     *
     * @param id The ProductCategoryKey representing the composite ID (ProductID and CategoryID) to retrieve.
     * @return The ProductCategory object if found, or null if not found.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the composite ID is null or contains non-positive values.
     */
    @Override
    public ProductCategory getById(ProductCategory.ProductCategoryKey id) throws SQLException {
        if (id == null || id.getProductId() <= 0 || id.getCategoryId() <= 0) {
            throw new IllegalArgumentException("ProductID and CategoryID in ProductCategoryKey must be positive integers.");
        }

        String sql = "SELECT ProductID, CategoryID FROM ProductCategory WHERE ProductID = ? AND CategoryID = ?";
        ProductCategory productCategory = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id.getProductId());
            ps.setInt(2, id.getCategoryId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productCategory = extractProductCategoryFromResultSet(rs);
                }
            }
        }
        return productCategory;
    }

    /**
     * Retrieves all product category associations from the database, optionally ordered by specified columns.
     *
     * @param order The column names to order the results by (e.g., "ProductID, CategoryID"). Pass null or an empty string for no specific order.
     * @return A collection of ProductCategory objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<ProductCategory> getAll(String order) throws SQLException {
        Collection<ProductCategory> productCategories = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ProductID, CategoryID FROM ProductCategory");
        String actualOrderColumns = DEFAULT_ORDER_COLUMN;

        if (order != null && !order.trim().isEmpty()) {
            String[] parts = order.trim().split(",");
            boolean allAllowed = true;
            for (String part : parts) {
                if (!ALLOWED_ORDER_COLUMNS.contains(part.trim())) {
                    allAllowed = false;
                    System.err.println("Warning: Attempted to order by invalid column: '" + part.trim() + "'. Falling back to default order.");
                    break;
                }
            }
            if (allAllowed) {
                actualOrderColumns = order.trim();
            }
        }
        sql.append(" ORDER BY ").append(actualOrderColumns);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productCategories.add(extractProductCategoryFromResultSet(rs));
            }
        }
        return productCategories;
    }

    @Override
    public List<String> getAllowedOrderColumns() {

        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    /**
     * Helper method to extract a ProductCategory object from a ResultSet.
     *
     * @param rs The ResultSet containing product category data.
     * @return A populated ProductCategory object.
     * @throws SQLException if a database access error occurs.
     */
    private ProductCategory extractProductCategoryFromResultSet(ResultSet rs) throws SQLException {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProductId(rs.getInt("ProductID"));
        productCategory.setCategoryId(rs.getInt("CategoryID"));
        return productCategory;
    }
}