package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dto.CartItemDTO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.util.CartManager;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/cart/update")
public class UpdateCartServlet extends HttpServlet {
    // TODO: Here and in AddToCart check that quantity isn't more than stock. Also check in checkout and similar
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cartItemIdParam = request.getParameter("cartItemId");
        String quantityParam = request.getParameter("quantity");


        try {
            if (cartItemIdParam == null || quantityParam == null) {
                NotificationUtil.sendNotification(request, "Parametri mancanti. Impossible aggiornare il carrello.", "error");
                throw new RuntimeException("");
            }
            int cartItemId = Integer.parseInt(cartItemIdParam);
            int quantity = Integer.parseInt(quantityParam);
            var datasource = (DataSource) getServletContext().getAttribute("ds");
            if (quantity < 1) {
                NotificationUtil.sendNotification(request, "Quantità non valida. Impossibile aggiornare il carrello.", "error");
                throw new RuntimeException("");
            }
            CartItemDAO cartItemDAO = new CartItemDAO(datasource);
            ProductDAO productDAO = new ProductDAO(datasource);
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            ProductDTO productDTO = null;
            if (userId == null) {
                productDTO = productDAO.getById(cartItemId);
            } else {
                CartItemDTO cartItemDTO = cartItemDAO.getById(cartItemId);
                if (cartItemDTO == null) {
                    NotificationUtil.sendNotification(request, "Prodotto non trovato nel carrello. Impossibile aggiornare il carrello.", "error");
                    throw new RuntimeException("");
                }
                productDTO = productDAO.getById(cartItemDTO.getProductId());
            }
            if (productDTO == null) {
                NotificationUtil.sendNotification(request, "Prodotto non trovato. Impossibile aggiornare il carrello.", "error");
                throw new RuntimeException("");
            }
            if (productDTO.getStockQuantity() < quantity) {
                quantity = productDTO.getStockQuantity();
            }
            CartDAO cartDAO = new CartDAO(datasource);
            CartManager.updateItemQuantity(request, cartDAO, cartItemDAO, cartItemId, quantity);


        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "Formato del parametro non valido", "error");
        } catch (SQLException e) {
            NotificationUtil.sendNotification(request, "Errore durante l'aggiornamento del database", "error");
        } catch (Exception e) {
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
