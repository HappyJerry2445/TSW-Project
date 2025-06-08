// AddressesServlet.java
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

@WebServlet("/common/addresses")
public class AddressesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var addressDAO = new AddressDAO((DataSource) getServletContext().getAttribute("ds"));
        var session = req.getSession();
        var userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        if (userId == null) {
            NotificationUtil.sendNotification(req, "Devi essere loggato per visualizzare i tuoi indirizzi.", "error");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            Collection<AddressDTO> addresses = addressDAO.getAddressesByUserId(userId);
            req.setAttribute("addresses", addresses);
        } catch (SQLException e) {
            errors.add("Errore durante il recupero degli indirizzi.");
            req.setAttribute("errors", errors);
            e.printStackTrace();
        }

        req.getRequestDispatcher("/WEB-INF/views/common/addresses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // This method can be implemented later for adding/editing/deleting addresses
        doGet(req, resp); // For now, just display addresses on POST
    }
}