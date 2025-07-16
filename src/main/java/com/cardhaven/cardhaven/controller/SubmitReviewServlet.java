package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dao.ReviewDAO;
import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.ReviewDTO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

@WebServlet("/reviews/submit")
public class SubmitReviewServlet extends HttpServlet {

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String productIdStr = request.getParameter("productId");

        // 1. Authentication Check: User must be logged in.
        if (userId == null) {
            NotificationUtil.sendNotification(
                request,
                "Devi effettuare il login per lasciare una recensione.",
                "error"
            );
            String redirectUrl = (productIdStr != null &&
                    !productIdStr.isEmpty())
                ? request.getContextPath() + "/products/detail/" + productIdStr
                : request.getContextPath() + "/login";
            response.sendRedirect(redirectUrl);
            return;
        }

        // 2. Parameter Retrieval & Validation
        int productId = 0;
        try {
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "ID prodotto non valido."
            );
            return;
        }

        String ratingStr = request.getParameter("rating");
        String title = request.getParameter("title");
        String reviewText = request.getParameter("reviewText");
        List<String> errors = new ArrayList<>();

        int rating = 0;
        if (ratingStr == null || ratingStr.isEmpty()) {
            errors.add("La valutazione è obbligatoria.");
        } else {
            try {
                rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    errors.add(
                        "La valutazione deve essere un numero intero tra 1 e 5."
                    );
                }
            } catch (NumberFormatException e) {
                errors.add("Formato della valutazione non valido.");
            }
        }

        if (title == null || title.trim().isEmpty()) {
            errors.add("Il titolo della recensione è obbligatorio.");
        } else if (title.length() > 255) {
            errors.add("Il titolo non può superare i 255 caratteri.");
        }

        if (reviewText == null || reviewText.trim().isEmpty()) {
            errors.add("Il testo della recensione è obbligatorio.");
        }

        if (!errors.isEmpty()) {
            NotificationUtil.sendNotification(
                request,
                String.join(" ", errors),
                "error"
            );
            response.sendRedirect(
                request.getContextPath() + "/products/detail/" + productId
            );
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        OrderDAO orderDAO = new OrderDAO(ds);
        ReviewDAO reviewDAO = new ReviewDAO(ds);

        try {
            // 3. Authorization Check: User must have purchased the product.
            if (!orderDAO.hasUserPurchasedProduct(userId, productId)) {
                NotificationUtil.sendNotification(
                    request,
                    "Puoi recensire solo i prodotti che hai acquistato e ricevuto.",
                    "warning"
                );
                response.sendRedirect(
                    request.getContextPath() + "/products/detail/" + productId
                );
                return;
            }

            // 4. Create and Save the Review DTO
            ReviewDTO newReview = new ReviewDTO();
            newReview.setProductId(productId);
            newReview.setUserId(userId);
            newReview.setRating(rating);
            newReview.setTitle(title.trim());
            newReview.setReviewText(reviewText.trim());
            newReview.setCreatedAt(LocalDateTime.now());
            newReview.setReviewStatus(ReviewDTO.ReviewStatus.Pending); // All reviews start as pending

            reviewDAO.save(newReview);

            // 5. Provide feedback and redirect
            NotificationUtil.sendNotification(
                request,
                "Grazie! La tua recensione è stata inviata e sarà pubblicata dopo approvazione.",
                "success"
            );
            response.sendRedirect(
                request.getContextPath() + "/products/detail/" + productId
            );
        } catch (SQLException e) {
            // Handle specific database errors, like unique constraint for user+product review
            if (e.getSQLState().equals("23000")) {
                // Integrity constraint violation
                NotificationUtil.sendNotification(
                    request,
                    "Hai già recensito questo prodotto.",
                    "warning"
                );
            } else {
                NotificationUtil.sendNotification(
                    request,
                    "Si è verificato un errore del database. Riprova più tardi.",
                    "error"
                );
                e.printStackTrace(); // Log the full error for debugging
            }
            response.sendRedirect(
                request.getContextPath() + "/products/detail/" + productId
            );
        }
    }
}
