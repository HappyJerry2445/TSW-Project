package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ReviewDAO;
import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.model.dto.ReviewDTO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet(name = "AdminReviewsServlet", value = "/admin/reviews")
public class AdminReviewsServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(AdminReviewsServlet.class);
    private ReviewDAO reviewDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        reviewDAO = new ReviewDAO(dataSource);
        userDAO = new UserDAO(dataSource);
        productDAO = new ProductDAO(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();

        // Filter parameters
        String userEmailFilter = request.getParameter("reviewsUserEmail");
        String productNameFilter = request.getParameter("productName");
        String reviewStatusFilterStr = request.getParameter("reviewStatus");
        String minRatingFilterStr = request.getParameter("minRating");
        String maxRatingFilterStr = request.getParameter("maxRating");

        Integer userIdFilter = null;
        Integer productIdFilter = null;
        ReviewDTO.ReviewStatus reviewStatusFilter = null;
        Integer minRatingFilter = null;
        Integer maxRatingFilter = null;

        try {
            if (userEmailFilter != null && !userEmailFilter.isEmpty()) {
                UserDTO user = userDAO.getUserByEmail(userEmailFilter);
                if (user != null) {
                    userIdFilter = user.getId();
                    request.setAttribute("userEmail", userEmailFilter); // For repopulating form
                } else {
                    errors.add("Utente con email '" + userEmailFilter + "' non trovato. Il filtro utente non è stato applicato.");
                }
            }

            if (productNameFilter != null && !productNameFilter.isEmpty()) {
                // This is a simplified search for a product by name. In a real app, you might use a more robust search.
                Collection<ProductDTO> products = productDAO.getAll("ProductName");
                ProductDTO foundProduct = products.stream()
                        .filter(p -> p.getProductName().equalsIgnoreCase(productNameFilter))
                        .findFirst()
                        .orElse(null);
                if (foundProduct != null) {
                    productIdFilter = foundProduct.getProductId();
                    request.setAttribute("productName", productNameFilter); // For repopulating form
                } else {
                    errors.add("Prodotto con nome '" + productNameFilter + "' non trovato. Il filtro prodotto non è stato applicato.");
                }
            }

            if (reviewStatusFilterStr != null && !reviewStatusFilterStr.isEmpty()) {
                try {
                    reviewStatusFilter = ReviewDTO.ReviewStatus.valueOf(reviewStatusFilterStr);
                    request.setAttribute("reviewStatus", reviewStatusFilterStr); // For repopulating form
                } catch (IllegalArgumentException e) {
                    errors.add("Stato recensione non valido.");
                }
            }

            if (minRatingFilterStr != null && !minRatingFilterStr.isEmpty()) {
                try {
                    minRatingFilter = Integer.parseInt(minRatingFilterStr);
                    if (minRatingFilter < 1 || minRatingFilter > 5) {
                        errors.add("La valutazione minima deve essere tra 1 e 5.");
                        minRatingFilter = null;
                    }
                    request.setAttribute("minRating", minRatingFilterStr); // For repopulating form
                } catch (NumberFormatException e) {
                    errors.add("Valutazione minima non valida.");
                }
            }

            if (maxRatingFilterStr != null && !maxRatingFilterStr.isEmpty()) {
                try {
                    maxRatingFilter = Integer.parseInt(maxRatingFilterStr);
                    if (maxRatingFilter < 1 || maxRatingFilter > 5) {
                        errors.add("La valutazione massima deve essere tra 1 e 5.");
                        maxRatingFilter = null;
                    }
                    request.setAttribute("maxRating", maxRatingFilterStr); // For repopulating form
                } catch (NumberFormatException e) {
                    errors.add("Valutazione massima non valida.");
                }
            }


            Collection<ReviewDTO> reviews = reviewDAO.getFilteredReviews("CreatedAt", true, productIdFilter, userIdFilter, reviewStatusFilter, minRatingFilter, maxRatingFilter);

            Map<Integer, UserDTO> userMap = new HashMap<>();
            Map<Integer, ProductDTO> productMap = new HashMap<>();

            for (ReviewDTO review : reviews) {
                if (!userMap.containsKey(review.getUserId())) {
                    userMap.put(review.getUserId(), userDAO.getById(review.getUserId()));
                }
                if (!productMap.containsKey(review.getProductId())) {
                    productMap.put(review.getProductId(), productDAO.getById(review.getProductId()));
                }
            }

            request.setAttribute("reviews", reviews);
            request.setAttribute("userMap", userMap);
            request.setAttribute("productMap", productMap);
            request.setAttribute("reviewStatuses", ReviewDTO.ReviewStatus.values());
            request.setAttribute("errors", errors);

            request.getRequestDispatcher("/WEB-INF/views/admin/reviews.jsp").forward(request, response);

        } catch (SQLException e) {
            errors.add("Errore del database durante il recupero delle recensioni: " + e.getMessage());
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/admin/reviews.jsp").forward(request, response);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            NotificationUtil.sendNotification(request, "Azione non specificata.", "error");
            response.sendRedirect(request.getContextPath() + "/admin/reviews");
            return;
        }

        int reviewId;
        try {
            reviewId = Integer.parseInt(request.getParameter("reviewId"));
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "ID recensione non valido.", "error");
            response.sendRedirect(request.getContextPath() + "/admin/reviews");
            return;
        }

        try {
            ReviewDTO review = reviewDAO.getById(reviewId);
            if (review == null) {
                NotificationUtil.sendNotification(request, "Recensione non trovata.", "error");
                response.sendRedirect(request.getContextPath() + "/admin/reviews");
                return;
            }

            switch (action) {
                case "updateStatus":
                    String newStatusStr = request.getParameter("newStatus");
                    if (newStatusStr == null || newStatusStr.isEmpty()) {
                        NotificationUtil.sendNotification(request, "Stato recensione non specificato.", "error");
                    } else {
                        ReviewDTO.ReviewStatus newStatus = ReviewDTO.ReviewStatus.valueOf(newStatusStr);
                        review.setReviewStatus(newStatus);
                        reviewDAO.save(review);
                        NotificationUtil.sendNotification(request, "Stato recensione aggiornato con successo!", "success");
                    }
                    break;
                case "delete":
                    boolean deleted = reviewDAO.delete(reviewId);
                    if (deleted) {
                        NotificationUtil.sendNotification(request, "Recensione eliminata con successo.", "success");
                    } else {
                        NotificationUtil.sendNotification(request, "Impossibile eliminare la recensione.", "error");
                    }
                    break;
                default:
                    NotificationUtil.sendNotification(request, "Azione non valida.", "error");
                    break;
            }

        } catch (SQLException e) {
            NotificationUtil.sendNotification(request, "Errore del database: " + e.getMessage(), "error");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            NotificationUtil.sendNotification(request, "Stato recensione non valido: " + e.getMessage(), "error");
        }
        response.sendRedirect(request.getContextPath() + "/admin/reviews" + getFilterQueryString(request));
    }

    // Helper to preserve filters on redirect
    private String getFilterQueryString(HttpServletRequest request) {
        StringBuilder queryString = new StringBuilder("?");
        String userEmail = request.getParameter("reviewsUserEmail");
        String productName = request.getParameter("productName");
        String reviewStatus = request.getParameter("reviewStatus");
        String minRating = request.getParameter("minRating");
        String maxRating = request.getParameter("maxRating");

        if (userEmail != null && !userEmail.isEmpty()) {
            queryString.append("reviewsUserEmail=").append(userEmail).append("&");
        }
        if (productName != null && !productName.isEmpty()) {
            queryString.append("productName=").append(productName).append("&");
        }
        if (reviewStatus != null && !reviewStatus.isEmpty()) {
            queryString.append("reviewStatus=").append(reviewStatus).append("&");
        }
        if (minRating != null && !minRating.isEmpty()) {
            queryString.append("minRating=").append(minRating).append("&");
        }
        if (maxRating != null && !maxRating.isEmpty()) {
            queryString.append("maxRating=").append(maxRating).append("&");
        }

        if (queryString.length() > 1) { // Remove trailing '&' or '?' if no params
            return queryString.substring(0, queryString.length() - 1);
        }
        return "";
    }
}
