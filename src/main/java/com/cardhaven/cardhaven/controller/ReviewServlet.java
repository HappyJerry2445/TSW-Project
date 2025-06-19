package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.AddressDAO;
import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dto.AddressDTO;
import com.cardhaven.cardhaven.model.dto.CartDTO;
import com.cardhaven.cardhaven.model.dto.CartItemDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/common/checkout/review")
public class ReviewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession();
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

        try {
            // Fetch the full address objects
            AddressDTO shippingAddress = addressDAO.getById(shippingAddressId);
            AddressDTO billingAddress = addressDAO.getById(billingAddressId);

            // Fetch the user's cart
            CartDTO cart = cartDAO.getByUserId(userId);
            Collection<CartItemDTO> cartItems = null;

            if (cart != null) {
                // If the cart exists, fetch all items within that cart
                cartItems = cartItemDAO.getByCartId(cart.getCartId(), "AddedAT");
            }

            // If there is no cart or the cart is empty, redirect the user
            if (cart == null || cartItems == null || cartItems.isEmpty()) {
                NotificationUtil.sendNotification(request, "Il tuo carrello è vuoto. Aggiungi articoli prima di procedere.", "warning");
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            // Set attributes for the JSP to display
            request.setAttribute("shippingAddress", shippingAddress);
            request.setAttribute("billingAddress", billingAddress);
            request.setAttribute("cart", cart);
            request.setAttribute("cartItems", cartItems);

            // Forward to the review JSP page
            request.getRequestDispatcher("/WEB-INF/views/common/checkout/review.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            NotificationUtil.sendNotification(request, "Errore nel recupero dei dati per il riepilogo.", "error");
            //response.sendRedirect(request.getContextPath() + "/cart"); // Redirect to cart on error
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // For simplicity, the POST request just re-routes to the GET method.
        // In a real application, this might handle the final order confirmation logic.
        doGet(request, response);
    }
}