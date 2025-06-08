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

@WebServlet("/common/addresses/set-default")
public class SetDefaultAddressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        var userId = (Integer) session.getAttribute("userId");
        var userDAO = new UserDAO((DataSource) getServletContext().getAttribute("ds"));
        UserDTO loggedInUser;
        try {
            loggedInUser = userDAO.getById(userId);
        } catch (SQLException e) {
            throw new RuntimeException();
        }

        // 1. Check if user is logged in
        if (loggedInUser == null) {
            NotificationUtil.sendNotification(req, "Sessione scaduta. Si prega di accedere nuovamente.", "error");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // 2. Get the address ID from the request
        String addressIdParam = req.getParameter("addressId");
        if (addressIdParam == null || addressIdParam.isEmpty()) {
            NotificationUtil.sendNotification(req, "ID indirizzo non specificato.", "error");
            resp.sendRedirect(req.getContextPath() + "/common/addresses"); // Redirect back to addresses page
            return;
        }

        int addressId;
        try {
            addressId = Integer.parseInt(addressIdParam);
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(req, "ID indirizzo non valido.", "error");
            resp.sendRedirect(req.getContextPath() + "/common/addresses");
            return;
        }

        // 3. Initialize AddressDAO
        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        AddressDAO addressDAO = new AddressDAO(dataSource);

        try {
            // 4. Verify the address belongs to the logged-in user (important security step)
            // Assuming AddressDAO has a method to get an address by ID which also contains userId
            AddressDTO addressToSetDefault = addressDAO.getById(addressId); // Assuming you have an Address model class

            if (addressToSetDefault == null || addressToSetDefault.getUserID() != loggedInUser.getId()) {
                NotificationUtil.sendNotification(req, "Indirizzo non trovato o non di proprietà dell'utente.", "error");
                resp.sendRedirect(req.getContextPath() + "/common/addresses");
                return;
            }

            // 5. Set the address as default
            boolean success = addressDAO.setDefaultAddress(loggedInUser.getId(), addressId, addressToSetDefault.getAddressType());

            if (success) {
                NotificationUtil.sendNotification(req, "Indirizzo predefinito impostato con successo!", "success");
            } else {
                NotificationUtil.sendNotification(req, "Impossibile impostare l'indirizzo predefinito. Riprova.", "error");
            }

        } catch (SQLException e) {
            NotificationUtil.sendNotification(req, "Errore del database durante l'aggiornamento dell'indirizzo.", "error");
            e.printStackTrace(); // Log the exception for debugging
        }

        // 6. Redirect back to the addresses page
        resp.sendRedirect(req.getContextPath() + "/common/addresses");

    }
}