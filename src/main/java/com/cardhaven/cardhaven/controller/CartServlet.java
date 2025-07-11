package com.cardhaven.cardhaven.controller;


import com.cardhaven.cardhaven.model.dao.AddressDAO;
import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.CartItemDTO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        UserDAO userDAO = new UserDAO(ds);
        AddressDAO addressDAO = new AddressDAO(ds);

        UserDTO user = null;
        if (userId != null) {
            try {
                user = userDAO.getById(userId);
            } catch (SQLException _) {
            }

        }
        Collection<CartItemDTO> cartItems = null;
        try {
            if (user != null) {
                var cartDAO = new CartDAO(ds);
                var cart = cartDAO.getByUserId(user.getId());
                var cartItemsDAO = new CartItemDAO(ds);
                cartItems = cartItemsDAO.getByCartId(cart.getCartId(), null);

                var sessionCart = (Collection<CartItemDTO>) session.getAttribute("guestCart");

                if (sessionCart != null && !sessionCart.isEmpty()) {
                    for (var sessionItem : sessionCart) {
                        sessionItem.setCartId(cart.getCartId());
                        sessionItem.setCartItemId(0);
                        cartItemsDAO.save(sessionItem);
                    }
                    session.removeAttribute("guestCart");
                }
            } else {
                cartItems = (Collection<CartItemDTO>) session.getAttribute("guestCart");
                if (cartItems == null) {
                    cartItems = new ArrayList<>();
                    session.setAttribute("guestCart", cartItems);
                }
            }
            request.setAttribute("cartItems", cartItems);
            request.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(request, response);

        } catch (SQLException e) {
            getServletContext().log("Error retrieving cart data", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to retrieve cart data.");
        }
    }
}
