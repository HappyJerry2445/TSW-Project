package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.AddressDAO;
import com.cardhaven.cardhaven.model.dto.AddressDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@WebServlet("/common/checkout/*")
public class CheckoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession();
        var userId = (Integer) session.getAttribute("userId");
        var addressDAO = new AddressDAO((DataSource) getServletContext().getAttribute("ds"));
        List<String> errors = new ArrayList<>();

        if (userId == null) {
            NotificationUtil.sendNotification(request, "Devi essere loggato per accedere al checkout.", "error");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String pathInfo = request.getPathInfo();
        if(pathInfo != null && pathInfo.equals("/shipping")) {
            try {
                Collection<AddressDTO> addresses = addressDAO.getAddressesByUserId(userId);
                request.setAttribute("addresses", addresses);
            } catch (SQLException e) {
                errors.add("Errore durante il recupero degli indirizzi.");
                request.setAttribute("errors", errors);
                e.printStackTrace();
            }

            request.getRequestDispatcher("/WEB-INF/views/common/checkout/shipping.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            //response.sendRedirect(request.getContextPath() + "/checkout");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession();
        var userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        if (userId == null) {
            NotificationUtil.sendNotification(request, "Devi essere loggato per accedere al checkout.", "error");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String shippingAddressIdStr = request.getParameter("shippingAddressId");
        String billingAddressIdStr = request.getParameter("billingAddressId");

        // Controlla che l'utente abbia fatto una selezione per entrambi
        if (shippingAddressIdStr == null || shippingAddressIdStr.isEmpty() ||
                billingAddressIdStr == null || billingAddressIdStr.isEmpty()) {

            errors.add("Devi selezionare sia un indirizzo di spedizione che uno di fatturazione.");

            return;
        }

        try {
            int shippingAddressId = Integer.parseInt(shippingAddressIdStr);
            int billingAddressId = Integer.parseInt(billingAddressIdStr);

            // Salva gli ID scelti nella sessione per i passi successivi del checkout
            session.setAttribute("shippingAddressId", shippingAddressId);
            session.setAttribute("billingAddressId", billingAddressId);

            // Reindirizza al prossimo passo
            //response.sendRedirect(request.getContextPath() + "/common/checkout/review");

        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "La selezione dell'indirizzo non è valida.", "error");
            doGet(request, response);
        }
    }
}
