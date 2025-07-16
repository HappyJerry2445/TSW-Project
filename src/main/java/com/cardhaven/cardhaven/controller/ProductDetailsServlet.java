package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.*;
import com.cardhaven.cardhaven.model.dto.*;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.sql.DataSource;

@WebServlet("/products/detail/*")
public class ProductDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        List<String> errors = new ArrayList<>();
        req.setAttribute("errors", errors);

        if (pathInfo == null || pathInfo.equals("/")) {
            errors.add("Id del prodotto non specificato.");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(
                req,
                "ID prodotto non valido.",
                "error"
            );
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        if (ds == null) {
            errors.add("Errore di configurazione del server.");
            req
                .getRequestDispatcher("/WEB-INF/views/product-details.jsp")
                .forward(req, resp);
            return;
        }

        // --- Instantiate all necessary DAOs ---
        ProductDAO productDAO = new ProductDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);
        TradingCardDAO tradingCardDAO = new TradingCardDAO(ds);
        AccessoryDAO accessoryDAO = new AccessoryDAO(ds);
        ReviewDAO reviewDAO = new ReviewDAO(ds);
        UserDAO userDAO = new UserDAO(ds);
        ProductCategoryDAO productCategoryDAO = new ProductCategoryDAO(ds);
        CategoryDAO categoryDAO = new CategoryDAO(ds);

        try {
            // 1. Fetch main product
            ProductDTO product = productDAO.getById(productId);
            if (product == null) {
                NotificationUtil.sendNotification(
                    req,
                    "Prodotto non trovato.",
                    "error"
                );
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }
            req.setAttribute("product", product);
            req.setAttribute("pageTitle", product.getProductName());

            // 2. Fetch all product images, ordered by SortOrder
            Collection<ProductImageDTO> images =
                productImageDAO.getAllByProductId(productId);
            req.setAttribute("productImages", images);

            // 3. Fetch specific details based on product type
            if (
                product.getProductType() == ProductDTO.ProductType.TradingCard
            ) {
                TradingCardDTO cardDetails = tradingCardDAO.getById(productId);
                req.setAttribute("tradingCardDetails", cardDetails);
            } else if (
                product.getProductType() == ProductDTO.ProductType.Accessory
            ) {
                AccessoryDTO accessoryDetails = accessoryDAO.getById(productId);
                req.setAttribute("accessoryDetails", accessoryDetails);
            }

            // 4. Fetch product categories
            Collection<ProductCategoryDTO> productCategoryLinks =
                productCategoryDAO.getAll("ProductID"); // Simplified, needs getByProductId
            List<CategoryDTO> productCategories = new ArrayList<>();
            for (ProductCategoryDTO link : productCategoryLinks) {
                if (link.getProductId() == productId) {
                    CategoryDTO category = categoryDAO.getById(
                        link.getCategoryId()
                    );
                    if (category != null) {
                        productCategories.add(category);
                    }
                }
            }
            req.setAttribute("productCategories", productCategories);

            // 5. Fetch reviews and their authors
            Collection<ReviewDTO> reviews = reviewDAO.getFilteredReviews(
                "CreatedAt",
                true,
                productId,
                null,
                ReviewDTO.ReviewStatus.Approved,
                null,
                null
            );
            Map<Integer, UserDTO> reviewAuthors = new HashMap<>();
            for (ReviewDTO review : reviews) {
                if (!reviewAuthors.containsKey(review.getUserId())) {
                    UserDTO author = userDAO.getById(review.getUserId());
                    if (author != null) {
                        UserDTO publicAuthor = new UserDTO();
                        publicAuthor.setFirstName(author.getFirstName());
                        publicAuthor.setLastName(author.getLastName());
                        reviewAuthors.put(author.getId(), publicAuthor);
                    }
                }
            }
            req.setAttribute("reviews", reviews);
            req.setAttribute("reviewAuthors", reviewAuthors);

            // Forward to the JSP
            req
                .getRequestDispatcher("/WEB-INF/views/product-details.jsp")
                .forward(req, resp);
        } catch (SQLException e) {
            errors.add(
                "Errore durante il recupero dei dettagli del prodotto: " +
                e.getMessage()
            );
            req
                .getRequestDispatcher("/WEB-INF/views/product-details.jsp")
                .forward(req, resp);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doGet(req, resp);
    }
}
