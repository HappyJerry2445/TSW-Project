package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.CategoryDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.CategoryDTO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.model.dto.ProductImageDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.sql.DataSource;

@WebServlet(name = "ProductsSearchServlet", value = "/products/search")
public class ProductsSearchServlet extends HttpServlet {

    private ProductDAO productDAO;
    private ProductImageDAO productImageDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute(
            "ds"
        );
        productDAO = new ProductDAO(dataSource);
        productImageDAO = new ProductImageDAO(dataSource);
        categoryDAO = new CategoryDAO(dataSource);
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();

        // Parametri di ricerca
        String query = request.getParameter("query"); // General search term (mapped to ProductName)
        String minPriceStr = request.getParameter("minPrice");
        String maxPriceStr = request.getParameter("maxPrice");
        String categoryIdStr = request.getParameter("category");
        String productTypeStr = request.getParameter("productType");

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        Integer categoryId = null;
        ProductDTO.ProductType productType = null;

        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                minPrice = new BigDecimal(minPriceStr);
                if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Il prezzo minimo non può essere negativo.");
                    minPrice = null;
                }
            }
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                maxPrice = new BigDecimal(maxPriceStr);
                if (maxPrice.compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Il prezzo massimo non può essere negativo.");
                    maxPrice = null;
                }
            }
            if (
                minPrice != null &&
                maxPrice != null &&
                minPrice.compareTo(maxPrice) > 0
            ) {
                errors.add(
                    "Il prezzo minimo non può essere maggiore del prezzo massimo."
                );
                minPrice = null; // Reset to avoid incorrect filtering
                maxPrice = null; // Reset to avoid incorrect filtering
            }

            if (categoryIdStr != null && !categoryIdStr.isEmpty()) {
                categoryId = Integer.parseInt(categoryIdStr);
                if (categoryDAO.getById(categoryId) == null) {
                    errors.add("Categoria selezionata non valida.");
                    categoryId = null;
                }
            }

            if (productTypeStr != null && !productTypeStr.isEmpty()) {
                try {
                    productType = ProductDTO.ProductType.valueOf(
                        productTypeStr
                    );
                } catch (IllegalArgumentException e) {
                    errors.add("Tipo prodotto selezionato non valido.");
                    productType = null;
                }
            }

            // Recupera prodotti filtrati
            Collection<ProductDTO> products = productDAO.getFilteredProducts(
                null, // Order by product name
                false,
                query,
                null,
                productType,
                categoryId,
                minPrice,
                maxPrice,
                null,
                null
            );

            // Recupera le prime immagini per i prodotti
            Map<Integer, ProductImageDTO> productImages = new HashMap<>();
            for (ProductDTO product : products) {
                ProductImageDTO image = productImageDAO.getFirstByProductId(
                    product.getProductId()
                );
                if (image != null) {
                    productImages.put(product.getProductId(), image);
                }
            }

            // Identifica quali dei prodotti filtrati sono in saldo
            Set<Integer> onSaleProductIds = productDAO
                .findOnSale(Integer.MAX_VALUE)
                .stream()
                .map(ProductDTO::getProductId)
                .collect(Collectors.toSet());

            // Recupera tutte le categorie per il filtro dropdown
            Collection<CategoryDTO> categories = categoryDAO.getAll(
                "CategoryName"
            );

            // Imposta attributi per la JSP
            request.setAttribute("products", products);
            request.setAttribute("productImages", productImages);
            request.setAttribute("onSaleProductIds", onSaleProductIds);
            request.setAttribute("categories", categories);
            request.setAttribute(
                "productTypes",
                ProductDTO.ProductType.values()
            );
            request.setAttribute("errors", errors);

            // Per ripopolare i campi del form dopo la ricerca
            request.setAttribute("query", query);
            request.setAttribute("minPrice", minPriceStr);
            request.setAttribute("maxPrice", maxPriceStr);
            request.setAttribute("selectedCategory", categoryIdStr); // String to match option value
            request.setAttribute("selectedProductType", productTypeStr); // String to match option value

            request
                .getRequestDispatcher("/WEB-INF/views/search.jsp")
                .forward(request, response);
        } catch (NumberFormatException e) {
            errors.add("Formato numerico non valido per prezzo o categoria.");
            request.setAttribute("errors", errors);
            request
                .getRequestDispatcher("/WEB-INF/views/search.jsp")
                .forward(request, response);
            e.printStackTrace();
        } catch (SQLException e) {
            errors.add(
                "Errore del database durante la ricerca dei prodotti: " +
                e.getMessage()
            );
            request.setAttribute("errors", errors);
            request
                .getRequestDispatcher("/WEB-INF/views/search.jsp")
                .forward(request, response);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        // La ricerca di solito è una GET, ma per coerenza reindirizziamo al doGet
        doGet(request, response);
    }
}
