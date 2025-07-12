package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.*;
import com.cardhaven.cardhaven.model.dto.*;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            UserDTO loggedInUser;
            try {
                loggedInUser = userDAO.getById(userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            ProductDAO productDAO = new ProductDAO(ds);

            // Recupera i dettagli dei prodotti per ogni item
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (CartItemDTO item : cartItems) {
                if (!productMap.containsKey(item.getProductId())){
                    ProductDTO product = productDAO.getById(item.getProductId());
                    if (product != null) {
                        productMap.put(item.getProductId(), product);
                    }
                }
            }

            ProductImageDAO productImageDAO = new ProductImageDAO(ds); // Instantiate ProductImageDAO

            Map<Integer, String> productImageMap = new HashMap<>();
/*            for (ProductDTO product : productMap.values()) {
                byte[] images = productImageDAO.getImagesByProductId(product.getProductId());
                ServletOutputStream out = response.getOutputStream();
                if(images != null){
                    out.write(images);
                    response.setContentType("image/jpeg");
                }

            }*/

            // Set attributes for the JSP to display
            request.setAttribute("loggedInUser", loggedInUser);
            request.setAttribute("shippingAddress", shippingAddress);
            request.setAttribute("billingAddress", billingAddress);
            request.setAttribute("cart", cart);
            request.setAttribute("cartItems", cartItems);
            request.setAttribute("productMap", productMap);
            request.setAttribute("productImageMap", productImageMap); // Add the new map to the request


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