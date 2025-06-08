package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dto.OrderDTO;
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

@WebServlet("/common/orders")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        List<String> errors = new ArrayList<>();
        request.setAttribute("errors", errors); // Initialize errors attribute early

        // Unified JSP paths
        String ordersListPagePath = "/WEB-INF/views/common/orders.jsp";


        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        if (ds == null) {
            errors.add("Errore di configurazione del server. Riprova più tardi.");
            request.getRequestDispatcher(ordersListPagePath).forward(request, response);
            return;
        }

        OrderDAO orderDAO = new OrderDAO(ds); // Local DAO instance

        var userId = (Integer) session.getAttribute("userId");

        try {
            // --- Logic for displaying the list of all user's orders ---
            List<OrderDTO> orders = orderDAO.getOrderByUserID(userId);
            request.setAttribute("orders", orders);
            request.getRequestDispatcher(ordersListPagePath).forward(request, response);
        } catch (SQLException ex) { // Catches SQLException from getOrderByUserID if orderIdParam is null
            errors.add("Errore durante il recupero degli ordini dal database. Riprova più tardi.");
            request.getRequestDispatcher(ordersListPagePath).forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}