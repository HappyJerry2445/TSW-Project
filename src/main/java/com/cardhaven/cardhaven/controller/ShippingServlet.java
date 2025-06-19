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

@WebServlet("/common/checkout/shipping")
public class ShippingServlet extends HttpServlet {

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

        try {
            Collection<AddressDTO> addresses = addressDAO.getAddressesByUserId(userId);
            request.setAttribute("addresses", addresses);
        } catch (SQLException e) {
            errors.add("Errore durante il recupero degli indirizzi.");
            request.setAttribute("errors", errors);
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/views/common/checkout/shipping.jsp").forward(request, response);
    }

        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var session = request.getSession();
        var userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            NotificationUtil.sendNotification(request, "La tua sessione è scaduta. Effettua nuovamente il login.", "error");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String shippingAddressIdStr = request.getParameter("shippingAddressId");
        String billingAddressIdStr;

        // Check if the "sameAsShipping" checkbox was ticked. Its value will be "on" if checked.
        String sameAsShipping = request.getParameter("sameAsShipping");

        if (sameAsShipping != null && sameAsShipping.equals("on")) {
            // If the checkbox is checked, the billing address is the same as the shipping address.
            billingAddressIdStr = shippingAddressIdStr;
        } else {
            // Otherwise, get the billing address from its separate dropdown.
            billingAddressIdStr = request.getParameter("billingAddressId");
        }

        // Validate that addresses were selected
        if (shippingAddressIdStr == null || shippingAddressIdStr.isEmpty() || billingAddressIdStr == null || billingAddressIdStr.isEmpty()) {
            NotificationUtil.sendNotification(request, "Devi selezionare un indirizzo di spedizione e fatturazione.", "error");
            doGet(request, response); // Show the page again with an error
            return;
        }

        try {
            int shippingAddressId = Integer.parseInt(shippingAddressIdStr);
            int billingAddressId = Integer.parseInt(billingAddressIdStr);

            // Save the chosen IDs in the session for the next step
            session.setAttribute("shippingAddressId", shippingAddressId);
            session.setAttribute("billingAddressId", billingAddressId);

            // Redirect to the review step
            response.sendRedirect(request.getContextPath() + "/common/checkout/review");

        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "La selezione dell'indirizzo non è valida.", "error");
            doGet(request, response);
        }
    }
}