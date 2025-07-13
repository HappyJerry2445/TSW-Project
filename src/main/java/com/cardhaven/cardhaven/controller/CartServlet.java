package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.CartItemDetailDTO;
import com.cardhaven.cardhaven.util.CartManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        // Instantiate all necessary DAOs
        CartDAO cartDAO = new CartDAO(ds);
        CartItemDAO cartItemDAO = new CartItemDAO(ds);
        ProductDAO productDAO = new ProductDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);

        try {
            // A single, clean call to the manager to get everything needed for the view
            List<CartItemDetailDTO> detailedCartItems = CartManager.getDetailedCartItems(
                    request, cartDAO, cartItemDAO, productDAO, productImageDAO
            );
            System.out.println(detailedCartItems);

            request.setAttribute("cartItems", detailedCartItems);
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);

        } catch (SQLException e) {
            getServletContext().log("Error retrieving detailed cart data using CartManager", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve your cart. Please try again later.");
        }
    }
}