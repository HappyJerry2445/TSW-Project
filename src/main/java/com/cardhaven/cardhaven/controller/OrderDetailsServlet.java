package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dao.OrderItemDAO;
import com.cardhaven.cardhaven.model.dto.OrderDTO;
import com.cardhaven.cardhaven.model.dto.OrderItemDTO;
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
import java.util.List;

@WebServlet("/common/orders/*")
public class OrderDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
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

        UserDTO user = (UserDTO) session.getAttribute("loggedInUser");

        try {
            OrderDTO order = orderDAO.getById(orderId);
            if (order == null || order.getUserId() != user.getId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ordine non trovato o non autorizzato");
                return;
            }
            List<OrderItemDTO> items = orderItemDAO.getOrderItemsByOrderId(orderId);

            request.setAttribute("order", order);
            request.setAttribute("orderItems", items);
            request.getRequestDispatcher("/WEB-INF/views/common/order_details.jsp").forward(request, response);
        } catch (SQLException ex) {
            throw new ServletException("Errore nel recupero dettagli ordine", ex);
        }

        //TODO show the product detail
    }

    private boolean isUserAuthenticated(HttpSession session) {
        // Checks for the "loggedInUser" attribute, consistent with LoginServlet.
        return session != null && session.getAttribute("loggedInUser") != null;
    }
}