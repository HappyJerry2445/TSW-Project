package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dao.OrderItemDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.AddressDAO;
import com.cardhaven.cardhaven.model.dto.OrderDTO;
import com.cardhaven.cardhaven.model.dto.OrderItemDTO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.model.dto.AddressDTO;
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
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/common/orders/*")
public class OrderDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        List<String> errors = new ArrayList<>();
        String loginPagePath = "/WEB-INF/views/login.jsp";

        if (!isUserAuthenticated(session)) {
            request.getRequestDispatcher(loginPagePath).forward(request, response);
            return;
        }

        String pathInfo = request.getPathInfo(); // /{orderId}
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ordine mancante");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ordine non valido");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        OrderDAO orderDAO = new OrderDAO(ds);
        OrderItemDAO orderItemDAO = new OrderItemDAO(ds);
        ProductDAO productDAO = new ProductDAO(ds);
        AddressDAO addressDAO = new AddressDAO(ds);

        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");

        try {
            OrderDTO order = orderDAO.getById(orderId);
            if (order == null || order.getUserId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ordine non trovato o non autorizzato");
                return;
            }

            // Recupera gli items dell'ordine
            List<OrderItemDTO> items = orderItemDAO.getOrderItemsByOrderId(orderId);

            // Recupera i dettagli dei prodotti per ogni item
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (OrderItemDTO item : items) {
                if (!productMap.containsKey(item.getProductID())) {
                    ProductDTO product = productDAO.getById(item.getProductID());
                    if (product != null) {
                        productMap.put(item.getProductID(), product);
                    }
                }
            }

            // Recupera l'indirizzo di spedizione
            AddressDTO shippingAddress = null;
            if (order.getShippingAddressId() > 0) {
                shippingAddress = addressDAO.getById(order.getShippingAddressId());
            }

            request.setAttribute("order", order);
            request.setAttribute("orderItems", items);
            request.setAttribute("productMap", productMap);
            request.setAttribute("shippingAddress", shippingAddress);
            request.getRequestDispatcher("/WEB-INF/views/common/order_details.jsp").forward(request, response);

        } catch (SQLException ex) {
            errors.add("Errore nel recupero dei dettagli dell'ordine.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/common/order_details.jsp").forward(request, response);
            ex.printStackTrace();
        }
    }

    private boolean isUserAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("loggedInUser") != null;
    }
}