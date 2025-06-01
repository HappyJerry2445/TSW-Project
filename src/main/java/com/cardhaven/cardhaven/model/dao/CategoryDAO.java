package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class CategoryDAO implements GenericDAO<Category, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "CategoryID", "CategoryName", "ParentCategoryID", "CategoryType", "Description"
    );
    private static final String DEFAULT_ORDER_COLUMN = "CategoryID"; // Or "CategoryName"


    private final DataSource dataSource;


    /**
     * Constructor for CategoryDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public CategoryDAO(DataSource dataSource) {
        // Ensure the DataSource is not null, as it's essential for database operations.
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves a Category bean to the database.
     * If the CategoryID is 0, it performs an INSERT operation.
     * Otherwise, it performs an UPDATE operation based on the CategoryID.
     *
     * @param category The Category object to save.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public void save(Category category) throws SQLException {
        // Input validation: Ensure critical fields are not null or empty
        if (category == null || category.getName() == null || category.getName().trim().isEmpty() ||
                category.getType() == null || category.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Category, CategoryName, or CategoryType cannot be null or empty.");
        }

        String sql;
        if (category.getId() == 0) { // New category (INSERT)
            sql = "INSERT INTO Category (CategoryName, ParentCategoryID, CategoryType, Description) VALUES (?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Get generated ID
                //noinspection DuplicatedCode
                ps.setString(1, category.getName());
                // Handle nullable ParentCategoryID
                if (category.getParentId() != null && category.getParentId() != 0) {
                    ps.setInt(2, category.getParentId());
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }
                ps.setString(3, category.getType());
                ps.setString(4, category.getDescription());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating category failed, no rows affected.");
                }

                // Retrieve the generated CategoryID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        category.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating category failed, no ID obtained.");
                    }
                }
            }
        } else { // Existing category (UPDATE)
            sql = "UPDATE Category SET CategoryName = ?, ParentCategoryID = ?, CategoryType = ?, Description = ? WHERE CategoryID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                //noinspection DuplicatedCode
                ps.setString(1, category.getName());
                // Handle nullable ParentCategoryID
                if (category.getParentId() != null && category.getParentId() != 0) {
                    ps.setInt(2, category.getParentId());
                } else {
                    ps.setNull(2, java.sql.Types.INTEGER);
                }
                ps.setString(3, category.getType());
                ps.setString(4, category.getDescription());
                ps.setInt(5, category.getId());

                ps.executeUpdate();
            }
        }
    }

    /**
     * Deletes a Category from the database.
     *
     * @param id The Category object to delete (CategoryID is used).
     * @return true if the category was deleted, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("CategoryID cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a Category from the database by its primary key (CategoryID).
     *
     * @param categoryID The ID of the category to retrieve.
     * @return The Category object if found, or null if not found.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Category getById(Integer categoryID) throws SQLException {
        if (categoryID == null || categoryID <= 0) {
            throw new IllegalArgumentException("CategoryID must be a positive integer.");
        }

        String sql = "SELECT CategoryID, CategoryName, ParentCategoryID, CategoryType, Description FROM Category WHERE CategoryID = ?";
        Category category = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    category = extractCategoryFromResultSet(rs);
                }
            }
        }
        return category;
    }

    /**
     * Retrieves all categories from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by (e.g., "CategoryName", "CategoryID").
     *              Pass null or an empty string for no specific order.
     * @return A collection of Category objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<Category> getAll(String order) throws SQLException {
        Collection<Category> categories = new ArrayList<>();
        // Basic validation for order parameter to prevent direct injection,
        // but robust whitelisting is recommended for dynamic sorting.
        String validOrderColumns = "CategoryID, CategoryName, ParentCategoryID, CategoryType, Description"; // Whitelist
        if (order != null && !order.trim().isEmpty() && !validOrderColumns.contains(order)) {
            // Log this attempt or throw an exception if the order string is not in your whitelist
            System.err.println("Warning: Attempted to order by invalid column: " + order);
            order = null; // Fallback to no order
        }

        StringBuilder sql = new StringBuilder("SELECT CategoryID, CategoryName, ParentCategoryID, CategoryType, Description FROM Category");
        String actualOrderColumn;
        if (order != null && !order.trim().isEmpty()) {
            String trimmedOrder = order.trim();
            if (ALLOWED_ORDER_COLUMNS.contains(trimmedOrder)) {
                actualOrderColumn = trimmedOrder;
            } else {
                System.err.println("Warning: Attempted to order by invalid column: '" + order + "'. Falling back to default/no order.");
                actualOrderColumn = DEFAULT_ORDER_COLUMN;
            }
        } else {
            actualOrderColumn = DEFAULT_ORDER_COLUMN;
        }
        sql.append(" ORDER BY ").append(actualOrderColumn);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                categories.add(extractCategoryFromResultSet(rs));
            }
        }
        return categories;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    /**
     * Helper method to extract a Category object from a ResultSet.
     *
     * @param rs The ResultSet containing category data.
     * @return A populated Category object.
     * @throws SQLException if a database access error occurs.
     */
    private Category extractCategoryFromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("CategoryID"));
        category.setName(rs.getString("CategoryName"));

        // Handle nullable ParentCategoryID
        int parentId = rs.getInt("ParentCategoryID");
        if (rs.wasNull()) {
            category.setParentId(null);
        } else {
            category.setParentId(parentId);
        }

        category.setType(rs.getString("CategoryType"));
        category.setDescription(rs.getString("Description"));
        return category;
    }
}