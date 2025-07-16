package com.cardhaven.cardhaven.model.dao;

import com.cardhaven.cardhaven.model.dto.ProductDTO;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDAO implements GenericDAO<ProductDTO, Integer> {

    private static final List<String> ALLOWED_ORDER_COLUMNS = Arrays.asList(
        "ProductId",
        "SKU",
        "ProductName",
        "BasePrice",
        "CurrentPrice",
        "StockQuantity",
        "ProductType",
        "CreatedAt",
        "LastUpdated",
        "IsActive",
        "ProductDescription",
        "score"
    );
    private static final String DEFAULT_ORDER_COLUMN = "ProductId";
    private static final Logger log = LoggerFactory.getLogger(ProductDAO.class);

    private final DataSource dataSource;

    public ProductDAO(DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(
            dataSource,
            "DataSource cannot be null."
        );
    }

    public void save(ProductDTO productDTO) throws SQLException {
        validateProduct(productDTO);

        String sql;
        if (productDTO.getProductId() == 0) {
            sql =
                "INSERT INTO Product (ProductId, SKU, ProductName, BasePrice, CurrentPrice, StockQuantity, ProductType, CreatedAt, LastUpdated, IsActive, ProductDescription) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
                );
            ) {
                ps.setInt(1, productDTO.getProductId());
                ps.setString(2, productDTO.getSku());
                ps.setString(3, productDTO.getProductName());
                ps.setBigDecimal(4, productDTO.getBasePrice());
                ps.setBigDecimal(5, productDTO.getCurrentPrice());
                ps.setDouble(6, productDTO.getStockQuantity());
                ps.setString(7, productDTO.getProductType().name());
                ps.setTimestamp(
                    8,
                    (productDTO.getCreatedAt() != null)
                        ? Timestamp.valueOf(productDTO.getCreatedAt())
                        : Timestamp.valueOf(LocalDateTime.now())
                );
                ps.setTimestamp(
                    9,
                    (productDTO.getLastUpdated() != null)
                        ? Timestamp.valueOf(productDTO.getLastUpdated())
                        : null
                );
                ps.setBoolean(10, productDTO.isActive());
                ps.setString(11, productDTO.getProductDescription());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Failed to insert product");
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        productDTO.setProductId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException(
                            "Creating product failed, no ID obtained."
                        );
                    }
                }
            }
        } else {
            sql =
                "UPDATE Product SET SKU = ?, ProductName = ?, BasePrice = ?, CurrentPrice = ?, StockQuantity = ?, ProductType = ?, CreatedAt = ?, LastUpdated = ?, IsActive = ? , ProductDescription = ? WHERE ProductId = ?";
            try (
                Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
            ) {
                ps.setString(1, productDTO.getSku());
                ps.setString(2, productDTO.getProductName());
                ps.setBigDecimal(3, productDTO.getBasePrice());
                ps.setBigDecimal(4, productDTO.getCurrentPrice());
                ps.setDouble(5, productDTO.getStockQuantity());
                ps.setString(6, productDTO.getProductType().name());
                ps.setTimestamp(
                    7,
                    (productDTO.getCreatedAt() != null)
                        ? Timestamp.valueOf(productDTO.getCreatedAt())
                        : null
                );
                ps.setTimestamp(
                    8,
                    (productDTO.getLastUpdated() != null)
                        ? Timestamp.valueOf(productDTO.getLastUpdated())
                        : null
                );
                ps.setBoolean(9, productDTO.isActive());
                ps.setString(10, productDTO.getProductDescription());
                ps.setInt(11, productDTO.getProductId());

                ps.executeUpdate();
            }
        }
    }

    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "Product ID cannot be null or zero."
            );
        }

        // Change from hard delete to soft delete
        String sql = "UPDATE Product SET IsActive = false WHERE ProductId = ?";
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    public ProductDTO getById(Integer id) throws SQLException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                "ProductID must be a positive integer."
            );
        }
        String sql = "SELECT * FROM Product WHERE ProductId = ?";
        ProductDTO productDTO = null;
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                productDTO = extractProductFromResultSet(rs);
            }
        }
        return productDTO;
    }

    public Collection<ProductDTO> getAll(String order) throws SQLException {
        return getFilteredProducts(
            order,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    /**
     * Retrieves a filtered collection of products from the database.
     *
     * @param order       The column name to order the results by.
     *                    Pass null or an empty string for default order.
     * @param desc        Descending or not
     * @param searchTerm  Optional. Filters products by product name (case-insensitive, partial match).
     * @param sku         Optional. Filters products by SKU (case-insensitive, partial match).
     * @param productType Optional. Filters products by product type.
     * @param categoryId  Optional. Filters products by category ID.
     * @param minPrice    Optional. Filters products with current price >= this value.
     * @param maxPrice    Optional. Filters products with current price <= this value.
     * @param minStock    Optional. Filters products with stock quantity >= this value.
     * @param maxStock    Optional. Filters products with stock quantity <= this value.
     * @return A collection of Product objects.
     * @throws SQLException if a database access error occurs.
     */
    public Collection<ProductDTO> getFilteredProducts(
        String order,
        boolean desc,
        String searchTerm,
        String sku,
        ProductDTO.ProductType productType,
        Integer categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Integer minStock,
        Integer maxStock
    ) throws SQLException {
        Collection<ProductDTO> productDTOS = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        boolean isFullTextSearch = (searchTerm != null &&
            !searchTerm.isEmpty());

        StringBuilder sql = new StringBuilder("SELECT p.*");
        if (isFullTextSearch) {
            sql.append(
                ", MATCH(p.ProductName, p.ProductDescription) AGAINST (?)"
            );
            params.add(searchTerm);
        }
        sql.append(
            " FROM Product p LEFT JOIN ProductCategory pc ON p.ProductId = pc.ProductID WHERE p.IsActive = true"
        );

        if (isFullTextSearch) {
            // Trasforma "smart tv" in "+smart +tv" per una ricerca booleana che richiede tutte le parole.
            String ftsQuery = Arrays.stream(searchTerm.trim().split("\\s+"))
                .map(word -> "+" + word)
                .reduce("", (a, b) -> a + " " + b)
                .trim();
            // La condizione di filtro
            sql.append(
                " AND MATCH(p.ProductName, p.ProductDescription) AGAINST (? IN BOOLEAN MODE)"
            );
            params.add(ftsQuery); // Aggiungi lo stesso parametro una seconda volta
        }

        if (sku != null && !sku.isEmpty()) {
            sql.append(" AND p.SKU LIKE ?");
            params.add("%" + sku + "%");
        }
        if (productType != null) {
            sql.append(" AND p.ProductType = ?");
            params.add(productType.name());
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND pc.CategoryID = ?");
            params.add(categoryId);
        }
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0) {
            sql.append(" AND p.CurrentPrice >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) >= 0) {
            sql.append(" AND p.CurrentPrice <= ?");
            params.add(maxPrice);
        }
        if (minStock != null && minStock >= 0) {
            sql.append(" AND p.StockQuantity >= ?");
            params.add(minStock);
        }
        if (maxStock != null && maxStock >= 0) {
            sql.append(" AND p.StockQuantity <= ?");
            params.add(maxStock);
        }

        sql.append(" GROUP BY p.ProductId");

        // === GESTIONE DELL'ORDINAMENTO ===
        String orderByClause;
        orderByClause = "p.ProductName ASC";

        log.debug("Order: {}", order);
        if (
            order != null &&
            !order.trim().isEmpty() &&
            ALLOWED_ORDER_COLUMNS.contains(order.trim())
        ) {
            // L'utente può sovrascrivere l'ordinamento di default
            // Non prefissare 'score' con 'p.' perché è un alias, non una colonna della tabella.
            String columnPrefix = order.trim().equals("score") ? "" : "p.";
            orderByClause =
                columnPrefix + order.trim() + (desc ? " DESC" : " ASC");
        }

        sql.append(" ORDER BY ").append(orderByClause);
        log.debug("SQL: {}", sql);

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString());
        ) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            log.debug("ps: {}", ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productDTOS.add(extractProductFromResultSet(rs));
            }
        }
        return productDTOS;
    }

    @Override
    public List<String> getAllowedOrderColumns() {
        return new ArrayList<>(ALLOWED_ORDER_COLUMNS);
    }

    private ProductDTO extractProductFromResultSet(ResultSet rs)
        throws SQLException {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(rs.getInt("ProductId"));
        productDTO.setSku(rs.getString("SKU"));
        productDTO.setProductName(rs.getString("ProductName"));
        productDTO.setBasePrice(rs.getBigDecimal("BasePrice"));
        productDTO.setCurrentPrice(rs.getBigDecimal("CurrentPrice"));
        productDTO.setStockQuantity(rs.getInt("StockQuantity"));
        productDTO.setProductType(
            ProductDTO.ProductType.valueOf(rs.getString("ProductType"))
        );
        Timestamp createdAtTimestamp = rs.getTimestamp("CreatedAt");
        if (createdAtTimestamp != null) {
            productDTO.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        Timestamp lastUpdatedTimestamp = rs.getTimestamp("LastUpdated");
        if (lastUpdatedTimestamp != null) {
            productDTO.setLastUpdated(lastUpdatedTimestamp.toLocalDateTime());
        }
        productDTO.setActive(rs.getBoolean("IsActive"));
        productDTO.setProductDescription(rs.getString("ProductDescription"));

        return productDTO;
    }

    private void validateProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException(
                "Il prodotto non può essere null."
            );
        }
        if (
            productDTO.getSku() == null || productDTO.getSku().trim().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "SKU non può essere null o vuoto."
            );
        }
        if (
            productDTO.getProductName() == null ||
            productDTO.getProductName().trim().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Il nome del prodotto non può essere null o vuoto."
            );
        }
        if (productDTO.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Prezzo base deve essere positivo"
            );
        }
        if (productDTO.getCurrentPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Prezzo corrente deve essere positivo"
            );
        }
        if (productDTO.getStockQuantity() < 0) {
            throw new IllegalArgumentException(
                "La stock quantity deve essere positiva o zero."
            );
        }
        if (productDTO.getProductType() == null) {
            throw new IllegalArgumentException(
                "Il tipo di prodotto non può essere null."
            );
        }
    }

    public Collection<ProductDTO> getProductsByCategory(int categoryId)
        throws SQLException {
        Collection<ProductDTO> products = new ArrayList<>();
        String sql = """
            SELECT *
            FROM Product
            JOIN ProductCategory ON Product.ProductId = ProductCategory.ProductID
            WHERE ProductCategory.CategoryID = ? AND Product.IsActive = true
            ORDER BY Product.ProductName
            """;
        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDTO product = extractProductFromResultSet(rs);
                    products.add(product);
                }
            }
        }
        return products;
    }

    public List<ProductDTO> findTopN(int n) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        // Ordina per data di creazione più recente
        String sql =
            "SELECT * FROM Product WHERE IsActive = true ORDER BY CreatedAt DESC LIMIT ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProductFromResultSet(rs));
                }
            }
        }
        return products;
    }

    public List<ProductDTO> findOnSale(int n) throws SQLException {
        List<ProductDTO> products = new ArrayList<>();
        // Seleziona prodotti dove il prezzo corrente è inferiore al prezzo base
        String sql =
            "SELECT * FROM Product WHERE IsActive = true AND CurrentPrice < BasePrice ORDER BY CreatedAt DESC LIMIT ?";

        try (
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, n);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProductFromResultSet(rs));
                }
            }
        }
        return products;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM `Product`";

        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
