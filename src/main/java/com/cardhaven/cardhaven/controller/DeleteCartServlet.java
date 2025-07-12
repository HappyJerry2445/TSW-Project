package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.util.CartManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cart/delete")
public class DeleteCartServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cartItemIdParam = request.getParameter("cartItemId");

        if (cartItemIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cart item ID mancante.");
            return;
        }

        try {
            int cartItemId = Integer.parseInt(cartItemIdParam);
            var datasource = (DataSource) getServletContext().getAttribute("ds");
            CartItemDAO cartItemDAO = new CartItemDAO(datasource);
            CartDAO cartDAO = new CartDAO(datasource);

            CartManager.deleteItem(request, cartDAO, cartItemDAO, cartItemId);

            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato del parametro non valido. Cart item ID: " + cartItemIdParam);
        } catch (SQLException e) {
            throw new ServletException("Errore del database", e);
        }
    }
}
