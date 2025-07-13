package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ProductImageDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class ProductImageDAO implements GenericDAO<ProductImageDTO, Integer> {
    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
            "ProductImageId", "ProductId", "SortOrder", "ImageId"
    );

    private static final String DEFAULT_ORDER_COLUMN = "ProductImageId";

    private final DataSource dataSource;

    public ProductImageDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource, "DataSource cannot be null.");
    }

    public void save(ProductImageDTO productImageDTO) throws SQLException {
        validateProductImage(productImageDTO);

        String sql;
        if (productImageDTO.getImageId() == 0) {
            sql = "INSERT INTO ProductImage (ProductImageId, ProductId, SortOrder, ImageID) VALUES (?, ?, ?, ?)";
            try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, productImageDTO.getProductImageId());
                ps.setInt(2, productImageDTO.getProductId());
                ps.setInt(3, productImageDTO.getSortOrder());
                ps.setInt(4, productImageDTO.getImageId());

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
            sql = "UPDATE ProductImage SET ProductId = ?, SortOrder = ?, ImageID = ?WHERE ProductImageId = ?";
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, productImageDTO.getProductId());
                ps.setInt(2, productImageDTO.getSortOrder());
                ps.setInt(3, productImageDTO.getImageId());
                ps.setInt(4, productImageDTO.getProductImageId());

                ps.executeUpdate();
            }
        }
    }

    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Image ID cannot be null or zero.");
        }
        String sql = "DELETE FROM ProductImage WHERE ProductImageId = ?";
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

        String sql = "SELECT * FROM ProductImage WHERE ProductImageID = ?";
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

    public ProductImageDTO getFirstByProductId(Integer productId) throws SQLException {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("ProductID must be a positive integer.");
        }

        String sql = "SELECT * FROM ProductImage WHERE ProductId = ? ORDER BY SortOrder ASC LIMIT 1";
        ProductImageDTO productImageDTO = null;
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productImageDTO = extractProductImageFromResultSet(rs);
                }
            }
        }
        return productImageDTO;
    }

    public Collection<ProductImageDTO> getAllByProductId(Integer productId) throws SQLException {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("ProductID must be a positive integer.");
        }

        String sql = "SELECT * FROM ProductImage WHERE ProductId = ? ORDER BY SortOrder";
        Collection<ProductImageDTO> productImageDTOs = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productImageDTOs.add(extractProductImageFromResultSet(rs));
                }
            }
        }
        return productImageDTOs;
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
        productImageDTO.setProductImageId(rs.getInt("ProductImageId"));
        productImageDTO.setProductId(rs.getInt("ProductId"));
        productImageDTO.setSortOrder(rs.getInt("SortOrder"));
        productImageDTO.setImageId(rs.getInt("ImageId"));

        return productImageDTO;
    }


    private void validateProductImage(ProductImageDTO productImageDTO) {
        if (productImageDTO == null) {
            throw new IllegalArgumentException("ProductImage cannot be null.");
        }
        if (productImageDTO.getProductId() <= 0) {
            throw new IllegalArgumentException("ProductId must be a positive integer.");
        }
        if (productImageDTO.getImageId() <= 0) {
            throw new IllegalArgumentException("ImageId must be a positive integer.");
        }
    }
}

