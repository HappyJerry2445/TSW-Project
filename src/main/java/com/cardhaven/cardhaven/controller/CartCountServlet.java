package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.CartItemDetailDTO;
import com.cardhaven.cardhaven.util.CartManager;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CartCountServlet", value = "/cart/count")
public class CartCountServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CartCountServlet.class.getName());
    private CartDAO cartDAO;
    private CartItemDAO cartItemDAO;
    private ProductDAO productDAO;
    private ProductImageDAO productImageDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        cartDAO = new CartDAO(dataSource);
        cartItemDAO = new CartItemDAO(dataSource);
        productDAO = new ProductDAO(dataSource);
        productImageDAO = new ProductImageDAO(dataSource);
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Map<String, Integer> result = new HashMap<>();
        int cartItemCount = 0;

        try {
            List<CartItemDetailDTO> detailedCartItems = CartManager.getDetailedCartItems(
                    request, cartDAO, cartItemDAO, productDAO, productImageDAO
            );

            for (CartItemDetailDTO item : detailedCartItems) {
                cartItemCount += item.getQuantity();
            }

            result.put("count", cartItemCount);
            out.print(gson.toJson(result));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore nel recupero del conteggio del carrello", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("count", 0); // Fallback to 0 or -1 to indicate error
            result.put("error", 1);
            out.print(gson.toJson(result));
        } finally {
            out.flush();
        }
    }
}

