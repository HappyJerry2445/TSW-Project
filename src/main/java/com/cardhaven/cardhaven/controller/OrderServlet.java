package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dto.OrderDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/common/orders")
public class OrderServlet extends HttpServlet {
    private OrderDAO orderDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isUserAuthenticated(session)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            int userId = (Integer) session.getAttribute("userId");
            List<OrderDTO> orders = orderDAO.getOrderByUserID(userId);

            request.setAttribute("orders", orders);
            request.getRequestDispatcher("/common/orders.jsp").forward(request, response);

        } catch (SQLException ex) {
            request.setAttribute("errorMessage", "Errore durante il recupero degli ordini");
            //request.getRequestDispatcher("/common/error.jsp").forward(request, response);
        }
    }

    private boolean isUserAuthenticated(HttpSession session) {
        return session != null && session.getAttribute("userId") != null;
    }
}
