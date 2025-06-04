// UserDAO.java
package com.cardhaven.cardhaven.model.dao; // Adjust package as needed

import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.password4j.Argon2Function;
import com.password4j.Password;
import com.password4j.types.Argon2;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserDAO implements GenericDAO<UserDTO, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "UserID", "FirstName", "LastName", "Email", "CreatedAt", "LastLogin", "Role"
    );
    private static final String DEFAULT_ORDER_COLUMN = "UserID"; // Or "LastName", "Email"

    private final DataSource dataSource;

    /**
     * Constructor for UserDAO.
     *
     * @param dataSource The DataSource to use for database connections.
     */
    public UserDAO(DataSource dataSource) {
        // Ensure the DataSource is not null, as it's essential for database operations.
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves a User bean to the database.
     * If the UserID is 0, it performs an INSERT operation.
     * Otherwise, it performs an UPDATE operation based on the UserID.
     *
     * @param userDTO The User object to save.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if user or essential fields are null/empty.
     */
    @Override
    public void save(UserDTO userDTO) throws SQLException {
        // Input validation: Ensure critical fields are not null or empty
        if (userDTO == null || userDTO.getFirstName() == null || userDTO.getFirstName().trim().isEmpty() ||
                userDTO.getLastName() == null || userDTO.getLastName().trim().isEmpty() ||
                userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty() ||
                userDTO.getPasswordHash() == null || userDTO.getPasswordHash().trim().isEmpty() ||
                userDTO.getRole() == null) {
            throw new IllegalArgumentException("User, FirstName, LastName, Email, PasswordHash, or Role cannot be null or empty.");
        }

        String sql;
        if (userDTO.getId() == 0) { // New user (INSERT)
            sql = "INSERT INTO User (FirstName, LastName, Email, PasswordHash, CreatedAt, LastLogin, Role) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Get generated ID
                ps.setString(1, userDTO.getFirstName());
                ps.setString(2, userDTO.getLastName());
                ps.setString(3, userDTO.getEmail());
                ps.setString(4, userDTO.getPasswordHash());
                // Set CreatedAt: use existing if provided, otherwise CURRENT_TIMESTAMP from DB or LocalDateTime.now()
                ps.setTimestamp(5, (userDTO.getCreatedAt() != null) ? Timestamp.valueOf(userDTO.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                // LastLogin can be null for a new user
                ps.setTimestamp(6, (userDTO.getLastLogin() != null) ? Timestamp.valueOf(userDTO.getLastLogin()) : null);
                ps.setString(7, userDTO.getRole().name()); // Store enum as string

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }

                // Retrieve the generated UserID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userDTO.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        } else { // Existing user (UPDATE)
            sql = "UPDATE User SET FirstName = ?, LastName = ?, Email = ?, PasswordHash = ?, LastLogin = ?, Role = ? WHERE UserID = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, userDTO.getFirstName());
                ps.setString(2, userDTO.getLastName());
                ps.setString(3, userDTO.getEmail());
                ps.setString(4, userDTO.getPasswordHash());
                ps.setTimestamp(5, (userDTO.getLastLogin() != null) ? Timestamp.valueOf(userDTO.getLastLogin()) : null);
                ps.setString(6, userDTO.getRole().name());
                ps.setInt(7, userDTO.getId());

                ps.executeUpdate();
            }
        }
    }

    /**
     * Deletes a User from the database.
     *
     * @param id The UserID to delete.
     * @return true if the user was deleted, false otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is null or zero.
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("UserID cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM User WHERE UserID = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves a User from the database by its primary key (UserID).
     *
     * @param userID The ID of the user to retrieve.
     * @return The User object if found, or null if not found.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the ID is not positive.
     */
    @Override
    public UserDTO getById(Integer userID) throws SQLException {
        if (userID == null || userID <= 0) {
            throw new IllegalArgumentException("UserID must be a positive integer.");
        }

        String sql = "SELECT UserID, FirstName, LastName, Email, PasswordHash, CreatedAt, LastLogin, Role FROM User WHERE UserID = ?";
        UserDTO userDTO = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userDTO = extractUserFromResultSet(rs);
                }
            }
        }
        return userDTO;
    }

    /**
     * Retrieves all users from the database, optionally ordered by a specified column.
     *
     * @param order The column name to order the results by (e.g., "Email", "UserID").
     *              Pass null or an empty string for no specific order, or to use default.
     * @return A collection of User objects.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public Collection<UserDTO> getAll(String order) throws SQLException {
        Collection<UserDTO> userDTOS = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT UserID, FirstName, LastName, Email, PasswordHash, CreatedAt, LastLogin, Role FROM User");

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
                userDTOS.add(extractUserFromResultSet(rs));
            }
        }
        return userDTOS;
    }

    public boolean verifyPassword(String plainTextPassword, String storedHashPassword) {
        var argon2id = Argon2Function.getInstance(19, 2, 1, 32, Argon2.ID);
        return Password.check(plainTextPassword, storedHashPassword).with(argon2id);
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user to retrieve.
     * @return The User object if found, null otherwise.
     * @throws SQLException             if a database access error occurs.
     * @throws IllegalArgumentException if the email is null or empty.
     */
    public UserDTO getUserByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty.");
        }

        String sql = "SELECT UserID, FirstName, LastName, Email, PasswordHash, CreatedAt, LastLogin, Role FROM User WHERE Email = ?";
        UserDTO userDTO = null;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userDTO = extractUserFromResultSet(rs);
                }
            }
        }
        return userDTO;
    }

    /**
     * Helper method to extract a User object from a ResultSet.
     *
     * @param rs The ResultSet containing user data.
     * @return A populated User object.
     * @throws SQLException if a database access error occurs.
     */
    private UserDTO extractUserFromResultSet(ResultSet rs) throws SQLException {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(rs.getInt("UserID"));
        userDTO.setFirstName(rs.getString("FirstName"));
        userDTO.setLastName(rs.getString("LastName"));
        userDTO.setEmail(rs.getString("Email"));
        userDTO.setPasswordHash(rs.getString("PasswordHash"));

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            userDTO.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        Timestamp lastLoginTimestamp = rs.getTimestamp("LastLogin");
        if (lastLoginTimestamp != null) {
            userDTO.setLastLogin(lastLoginTimestamp.toLocalDateTime());
        }
        // Assuming your Role enum has a valueOf method that matches the DB string
        userDTO.setRole(UserDTO.Role.valueOf(rs.getString("Role")));
        return userDTO;
    }
}