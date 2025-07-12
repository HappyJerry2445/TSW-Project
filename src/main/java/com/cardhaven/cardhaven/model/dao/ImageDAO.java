package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ImageDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class ImageDAO implements GenericDAO<ImageDTO, Integer> {


    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ImageId", "MimeType"
    );

    private static final String DEFAULT_ORDER_COLUMN = "ImageId";
    private final DataSource dataSource;

    public ImageDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    /**
     * Saves an image to the database and returns the generated ID.
     *
     * @param image The ImageDTO to save.
     * @throws SQLException If a database error occurs.
     */
    public void save(ImageDTO image) throws SQLException {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        if (image.getMimeType() == null || image.getMimeType().trim().isEmpty()) {
            throw new IllegalArgumentException("MimeType cannot be null or empty");
        }
        if (image.getImageData() == null || image.getImageData().length == 0) {
            throw new IllegalArgumentException("ImageData cannot be null or empty");
        }

        if (image.getImageId() > 0) {
            String updateSql = "UPDATE Image SET MimeType = ?, ImageData = ? WHERE ImageId = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, image.getMimeType());
                ps.setBytes(2, image.getImageData());
                ps.setInt(3, image.getImageId());
                if (ps.executeUpdate() == 0) {
                    throw new SQLException("Update failed, no rows affected.");
                }
            }
        } else {
            String insertSql = "INSERT INTO Image (MimeType, ImageData) VALUES (?, ?)";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, image.getMimeType());
                ps.setBytes(2, image.getImageData());
                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        image.setImageId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating image failed, no ID obtained.");
                    }
                }
            }
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id == 0) {
            throw new IllegalArgumentException("ImageID cannot be null or zero for deletion.");
        }

        String sql = "DELETE FROM Image WHERE ImageId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Retrieves an image by its ID.
     *
     * @param imageId The ID of the image to retrieve.
     * @return The ImageDTO, or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public ImageDTO getById(Integer imageId) throws SQLException {
        String sql = "SELECT * FROM Image WHERE ImageId = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, imageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ImageDTO image = new ImageDTO();
                    image.setImageId(rs.getInt("ImageId"));
                    image.setMimeType(rs.getString("MimeType"));
                    image.setImageData(rs.getBytes("ImageData"));
                    return image;
                }
            }
        }
        return null;
    }

    @Override
    public Collection<ImageDTO> getAll(String order) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM Image");

        if (order != null && !order.trim().isEmpty() && getAllowedOrderColumns().contains(order.trim())) {
            sql.append(" ORDER BY ").append(order.trim());
        }

        Collection<ImageDTO> images = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ImageDTO image = new ImageDTO();
                image.setImageId(rs.getInt("ImageId"));
                image.setMimeType(rs.getString("MimeType"));
                image.setImageData(rs.getBytes("ImageData"));
                images.add(image);
            }
        }
        return images;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return List.of("ImageId", "MimeType");
    }
}