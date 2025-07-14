package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.*;
import com.cardhaven.cardhaven.model.dto.*;
import com.cardhaven.cardhaven.util.CartManager;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/common/checkout/review")
public class ReviewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession();
        var userDAO = new UserDAO((DataSource) getServletContext().getAttribute("ds"));
        var userId = (Integer) session.getAttribute("userId");
        var ds = (DataSource) getServletContext().getAttribute("ds");

        // Check if user is logged in
        if (userId == null) {
            NotificationUtil.sendNotification(request, "Devi essere loggato per rivedere il tuo ordine.", "error");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer shippingAddressId = (Integer) session.getAttribute("shippingAddressId");
        Integer billingAddressId = (Integer) session.getAttribute("billingAddressId");

        // If addresses aren't selected yet, redirect back to the shipping step
        if (shippingAddressId == null || billingAddressId == null) {
            NotificationUtil.sendNotification(request, "Devi prima selezionare gli indirizzi di spedizione e fatturazione.", "error");
            response.sendRedirect(request.getContextPath() + "/common/checkout/shipping");
            return;
        }

        AddressDAO addressDAO = new AddressDAO(ds);
        CartDAO cartDAO = new CartDAO(ds);
        CartItemDAO cartItemDAO = new CartItemDAO(ds);
        ProductDAO productDAO = new ProductDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);

        try {
            // Fetch the full address objects
            AddressDTO shippingAddress = addressDAO.getById(shippingAddressId);
            AddressDTO billingAddress = addressDAO.getById(billingAddressId);


            List<CartItemDetailDTO> detailedCartItems = CartManager.getDetailedCartItems(
                    request, cartDAO, cartItemDAO, productDAO, productImageDAO
            );


            UserDTO loggedInUser;
            try {
                loggedInUser = userDAO.getById(userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            // Set attributes for the JSP to display
            request.setAttribute("loggedInUser", loggedInUser);
            request.setAttribute("shippingAddress", shippingAddress);
            request.setAttribute("billingAddress", billingAddress);
            request.setAttribute("cartItems", detailedCartItems);


            // Forward to the review JSP page
            request.getRequestDispatcher("/WEB-INF/views/common/checkout/review.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            NotificationUtil.sendNotification(request, "Errore nel recupero dei dati per il riepilogo.", "error");
            response.sendRedirect(request.getContextPath() + "/cart"); // Redirect to cart on error
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // For simplicity, the POST request just re-routes to the GET method.
        // In a real application, this might handle the final order confirmation logic.
        doGet(request, response);
    }
}