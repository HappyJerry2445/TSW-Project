package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.AddressDAO;
import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.AddressDTO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
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

// Register the servlet to handle requests at this URL
@WebServlet("/common/addresses/delete")
public class DeleteAddressServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        // 1. Check if user is authenticated
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        UserDAO userDAO = new UserDAO(ds);
        AddressDAO addressDAO = new AddressDAO(ds);

        UserDTO user = null;
        try {
            user = userDAO.getById(userId);
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
        } catch (SQLException e) {
            NotificationUtil.sendNotification(request, "Errore interno del database durante il recupero dell'utente. Contatta lo sviluppatore per assistenza.", "error");
            response.sendRedirect(request.getContextPath() + "/common/addresses");
            return;
        }

        String idStr = request.getParameter("addressId");
        if (idStr == null || idStr.trim().isEmpty()) {
            NotificationUtil.sendNotification(request, "ID indirizzo non valido.", "error");
            response.sendRedirect(request.getContextPath() + "/common/addresses");
            return;
        }


        try {
            int addressId = Integer.parseInt(idStr);

            // 3. Security Check: Verify the address belongs to the logged-in user
            AddressDTO address = addressDAO.getById(addressId);
            if (address == null || address.getUserID() != user.getId()) {
                NotificationUtil.sendNotification(request, "Indirizzo non trovato o non appartiene all'utente.", "error");
                response.sendRedirect(request.getContextPath() + "/common/addresses");
                return;
            }

            // 4. Perform the delete operation using the DAO
            boolean deleted = addressDAO.delete(addressId);

            if (deleted) {
                NotificationUtil.sendNotification(request, "Indirizzo eliminato con successo.", "info");
            } else {
                NotificationUtil.sendNotification(request, "Errore durante l'eliminazione dell'indirizzo.", "error");
                session.setAttribute("error", "Errore durante l'eliminazione dell'indirizzo.");
            }

        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "ID indirizzo non valido.", "error");
        } catch (SQLException e) {
            getServletContext().log("Database error deleting address", e);
            NotificationUtil.sendNotification(request, "Errore interno del database durante l'eliminazione dell'indirizzo.", "error");
        }
        response.sendRedirect(request.getContextPath() + "/common/addresses");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}