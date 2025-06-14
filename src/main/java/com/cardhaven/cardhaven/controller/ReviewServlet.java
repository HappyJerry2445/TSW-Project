package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.AddressDAO;
import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dto.AddressDTO;
import com.cardhaven.cardhaven.model.dto.CartDTO;
import com.cardhaven.cardhaven.model.dto.CartItemDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@WebServlet("/common/checkout/review")
public class ReviewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession();
        var userId = (session != null) ? (Integer) session.getAttribute("userId") : null;

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

        //TODO continue and resolve some bug


    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}