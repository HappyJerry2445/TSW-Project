package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.beans.ProductImage;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProductImageDAO implements GenericDAO<ProductImage, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ImageId", "ProductId", "ImageData", "MimeType", "Description", "SortOrder", "CreatedAt", "ThumbnailData", "thumbnailMimeType"
    );

    private static final String DEFAULT_ORDER_COLUMN = "ImageId";

    private final DataSource dataSource;

    public ProductImageDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(ProductImage productImage) throws SQLException {
        validateProductImage(productImage);

        String sql;
        if (productImage.getImageId() == 0) {
            sql = "INSERT INTO ProductImage (ImageId, ProductId, ImageData, MimeType, Description, SortOrder, CreatedAt, ThumbnailData, thumbnailMimeType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productImage.getImageId());
                ps.setInt(2, productImage.getProductId());
                ps.setBytes(3, productImage.getImageData());
                ps.setString(4, productImage.getMimeType());
                ps.setString(5, productImage.getDescription());
                ps.setInt(6, productImage.getSortOrder());
                ps.setTimestamp(7, (productImage.getCreatedAt() != null) ? Timestamp.valueOf(productImage.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                ps.setBytes(8, productImage.getThumbnailData());
                ps.setString(9, productImage.getThumbnailMimeType());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productImage.setImageId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating product image failed, no ID obtained.");
                    }
                }
            }
        } else {
            sql = "UPDATE ProductImage SET ProductId = ?, ImageData = ?, MimeType = ?, Description = ?, SortOrder = ?, CreatedAt = ?, ThumbnailData = ?, ThumbnailMimeType = ? WHERE ImageId = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, productImage.getProductId());
                ps.setBytes(2, productImage.getImageData());
                ps.setString(3, productImage.getMimeType());
                ps.setString(4, productImage.getDescription());
                ps.setInt(5, productImage.getSortOrder());
                ps.setTimestamp(6, (productImage.getCreatedAt() != null) ? Timestamp.valueOf(productImage.getCreatedAt()) : null);
                ps.setBytes(7, productImage.getThumbnailData());
                ps.setString(8, productImage.getThumbnailMimeType());
                ps.setInt(9, productImage.getImageId());

                ps.executeUpdate();
            }
        }
    }

    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Image ID cannot be null or zero.");
        }
        String sql = "DELETE FROM ProductImage WHERE ImageId = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public ProductImage getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ImageID must be a positive integer.");
        }

        String sql = "SELECT * FROM ProductImage WHERE ImageId = ?";
        ProductImage productImage = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productImage = extractProductImageFromResultSet(rs);
                }
            }
        }
        return productImage;
    }

    public Collection<ProductImage> getAll(String order) throws SQLException {
        Collection<ProductImage> images = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ProductImage");

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

        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                images.add(extractProductImageFromResultSet(rs));
            }
        }
        return images;
    }

    public List<String> getAllowedOrderColumns() {

        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    private ProductImage extractProductImageFromResultSet(ResultSet rs) throws SQLException {
        ProductImage productImage = new ProductImage();
        productImage.setImageId(rs.getInt("ImageId"));
        productImage.setProductId(rs.getInt("ProductId"));
        productImage.setImageData(rs.getBytes("ImageData"));
        productImage.setMimeType(rs.getString("MimeType"));
        productImage.setDescription(rs.getString("Description"));
        productImage.setSortOrder(rs.getInt("SortOrder"));

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            productImage.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        productImage.setThumbnailData(rs.getBytes("ThumbnailData"));
        productImage.setThumbnailMimeType(rs.getString("ThumbnailMimeType"));

        return productImage;
    }

    private void validateProductImage(ProductImage productImage) {
        if (productImage == null) {
            throw new IllegalArgumentException("ProductImage cannot be null.");
        }
        if (productImage.getProductId() <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive integer.");
        }
        if (productImage.getImageData() == null || productImage.getImageData().length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty.");
        }
        if (productImage.getMimeType() == null || productImage.getMimeType().trim().isEmpty()) {
            throw new IllegalArgumentException("MIME type cannot be null or empty.");
        }
        if (productImage.getCreatedAt() == null) {
            throw new IllegalArgumentException("CreatedAt timestamp cannot be null.");
        }
        if (productImage.getThumbnailData() == null || productImage.getThumbnailData().length == 0) {
            throw new IllegalArgumentException("Thumbnail Data data cannot be null or empty.");
        }
        if (productImage.getThumbnailMimeType() == null || productImage.getThumbnailMimeType().trim().isEmpty()) {
            throw new IllegalArgumentException("MIME type cannot be null or empty.");
        }
    }
}

