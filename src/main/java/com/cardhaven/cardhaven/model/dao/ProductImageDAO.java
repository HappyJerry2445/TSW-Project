package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ProductImageDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProductImageDAO implements GenericDAO<ProductImageDTO, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ImageId", "ProductId", "ImageData", "MimeType", "Description", "SortOrder", "CreatedAt", "ThumbnailData", "thumbnailMimeType"
    );

    private static final String DEFAULT_ORDER_COLUMN = "ImageId";

    private final DataSource dataSource;

    public ProductImageDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(ProductImageDTO productImageDTO) throws SQLException {
        validateProductImage(productImageDTO);

        String sql;
        if (productImageDTO.getImageId() == 0) {
            sql = "INSERT INTO ProductImage (ImageId, ProductId, ImageData, MimeType, Description, SortOrder, CreatedAt, ThumbnailData, thumbnailMimeType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productImageDTO.getImageId());
                ps.setInt(2, productImageDTO.getProductId());
                ps.setBytes(3, productImageDTO.getImageData());
                ps.setString(4, productImageDTO.getMimeType());
                ps.setString(5, productImageDTO.getDescription());
                ps.setInt(6, productImageDTO.getSortOrder());
                ps.setTimestamp(7, (productImageDTO.getCreatedAt() != null) ? Timestamp.valueOf(productImageDTO.getCreatedAt()) : Timestamp.valueOf(LocalDateTime.now()));
                ps.setBytes(8, productImageDTO.getThumbnailData());
                ps.setString(9, productImageDTO.getThumbnailMimeType());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productImageDTO.setImageId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating product image failed, no ID obtained.");
                    }
                }
            }
        } else {
            sql = "UPDATE ProductImage SET ProductId = ?, ImageData = ?, MimeType = ?, Description = ?, SortOrder = ?, CreatedAt = ?, ThumbnailData = ?, ThumbnailMimeType = ? WHERE ImageId = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, productImageDTO.getProductId());
                ps.setBytes(2, productImageDTO.getImageData());
                ps.setString(3, productImageDTO.getMimeType());
                ps.setString(4, productImageDTO.getDescription());
                ps.setInt(5, productImageDTO.getSortOrder());
                ps.setTimestamp(6, (productImageDTO.getCreatedAt() != null) ? Timestamp.valueOf(productImageDTO.getCreatedAt()) : null);
                ps.setBytes(7, productImageDTO.getThumbnailData());
                ps.setString(8, productImageDTO.getThumbnailMimeType());
                ps.setInt(9, productImageDTO.getImageId());

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

    public ProductImageDTO getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ImageID must be a positive integer.");
        }

        String sql = "SELECT * FROM ProductImage WHERE ImageId = ?";
        ProductImageDTO productImageDTO = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productImageDTO = extractProductImageFromResultSet(rs);
                }
            }
        }
        return productImageDTO;
    }

    public Collection<ProductImageDTO> getAll(String order) throws SQLException {
        Collection<ProductImageDTO> images = new ArrayList<>();
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

    private ProductImageDTO extractProductImageFromResultSet(ResultSet rs) throws SQLException {
        ProductImageDTO productImageDTO = new ProductImageDTO();
        productImageDTO.setImageId(rs.getInt("ImageId"));
        productImageDTO.setProductId(rs.getInt("ProductId"));
        productImageDTO.setImageData(rs.getBytes("ImageData"));
        productImageDTO.setMimeType(rs.getString("MimeType"));
        productImageDTO.setDescription(rs.getString("Description"));
        productImageDTO.setSortOrder(rs.getInt("SortOrder"));

        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            productImageDTO.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }

        productImageDTO.setThumbnailData(rs.getBytes("ThumbnailData"));
        productImageDTO.setThumbnailMimeType(rs.getString("ThumbnailMimeType"));

        return productImageDTO;
    }

    public Collection<ProductImageDTO> getImagesByProductId(Integer productId) throws SQLException {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive integer.");
        }

        Collection<ProductImageDTO> images = new ArrayList<>();
        String sql = "SELECT * FROM ProductImage WHERE ProductId = ? ORDER BY SortOrder, ImageId";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    images.add(extractProductImageFromResultSet(rs));
                }
            }
        }

        return images;
    }

    private void validateProductImage(ProductImageDTO productImageDTO) {
        if (productImageDTO == null) {
            throw new IllegalArgumentException("ProductImage cannot be null.");
        }
        if (productImageDTO.getProductId() <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive integer.");
        }
        if (productImageDTO.getImageData() == null || productImageDTO.getImageData().length == 0) {
            throw new IllegalArgumentException("Image data cannot be null or empty.");
        }
        if (productImageDTO.getMimeType() == null || productImageDTO.getMimeType().trim().isEmpty()) {
            throw new IllegalArgumentException("MIME type cannot be null or empty.");
        }
        if (productImageDTO.getCreatedAt() == null) {
            throw new IllegalArgumentException("CreatedAt timestamp cannot be null.");
        }
        if (productImageDTO.getThumbnailData() == null || productImageDTO.getThumbnailData().length == 0) {
            throw new IllegalArgumentException("Thumbnail Data data cannot be null or empty.");
        }
        if (productImageDTO.getThumbnailMimeType() == null || productImageDTO.getThumbnailMimeType().trim().isEmpty()) {
            throw new IllegalArgumentException("MIME type cannot be null or empty.");
        }
    }
}

