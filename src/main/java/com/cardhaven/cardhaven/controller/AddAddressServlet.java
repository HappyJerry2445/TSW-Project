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

@WebServlet("/common/addresses/add")
public class AddAddressServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        UserDAO userDAO = new UserDAO(dataSource);
        AddressDAO addressDAO = new AddressDAO(dataSource);

        try {
            UserDTO loggedInUser = userDAO.getById(userId);
            if (loggedInUser == null) {
                NotificationUtil.sendNotification(req, "Utente non trovato. Si prega di accedere nuovamente.", "error");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            req.setAttribute("addressTypes", AddressDTO.AddressType.values());
            req.getRequestDispatcher("/WEB-INF/views/common/add-address.jsp").forward(req, resp);

        } catch (SQLException e) {
            errors.add("Errore del database durante il recupero dell'utente.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/views/common/addresses.jsp").forward(req, resp);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        UserDAO userDAO = new UserDAO(dataSource);
        AddressDAO addressDAO = new AddressDAO(dataSource);

        try {
            UserDTO loggedInUser = userDAO.getById(userId);
            if (loggedInUser == null) {
                NotificationUtil.sendNotification(req, "Utente non trovato. Si prega di accedere nuovamente.", "error");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String street = req.getParameter("street");
            String city = req.getParameter("city");
            String state = req.getParameter("state");
            String postalCode = req.getParameter("postalCode");
            String country = req.getParameter("country");
            String addressTypeStr = req.getParameter("addressType");

            // Basic validation
            if (
                    street == null || street.isEmpty() ||
                            city == null || city.isEmpty() ||
                            postalCode == null || postalCode.isEmpty() ||
                            country == null || country.isEmpty() ||
                            addressTypeStr == null || addressTypeStr.isEmpty()
            ) {
                errors.add("Tutti i campi obbligatori dell'indirizzo devono essere compilati.");
            }

            int addressId = 0;

            AddressDTO.AddressType addressType = null;

            try {
                addressType = AddressDTO.AddressType.valueOf(addressTypeStr);
            } catch (IllegalArgumentException e) {
                errors.add("Tipo di indirizzo non valido.");
            }

            // If there are validation errors, set attributes and forward back to form
            if (!errors.isEmpty()) {
                req.setAttribute("errors", errors);
                // Re-populate the address object with submitted values for display
                AddressDTO submittedAddress = new AddressDTO();
                submittedAddress.setStreetAddress(street);
                submittedAddress.setCity(city);
                submittedAddress.setState(state);
                submittedAddress.setPostalCode(postalCode);
                submittedAddress.setCountry(country);
                req.setAttribute("address", submittedAddress); // Pass submitted data back to form
                req.getRequestDispatcher("/WEB-INF/views/common/add-address.jsp").forward(req, resp);
                return;
            }


            // Create an Address object with updated values
            AddressDTO updatedAddress = new AddressDTO();
            updatedAddress.setUserID(loggedInUser.getId()); // Set user ID for the update query's WHERE clause
            updatedAddress.setStreetAddress(street);
            updatedAddress.setCity(city);
            updatedAddress.setState(state);
            updatedAddress.setPostalCode(postalCode);
            updatedAddress.setCountry(country);
            updatedAddress.setAddressType(addressType);

            addressDAO.save(updatedAddress);

            NotificationUtil.sendNotification(req, "Indirizzo aggiunto con successo!", "success");
            resp.sendRedirect(req.getContextPath() + "/common/addresses"); // Redirect to address list

        } catch (SQLException e) {
            errors.add("Errore del database durante l'aggiunta dell'indirizzo.");
            req.setAttribute("errors", errors);
            // Re-populate the address object with submitted values for display in case of DB error
            AddressDTO submittedAddress = new AddressDTO();
            submittedAddress.setStreetAddress(req.getParameter("street"));
            submittedAddress.setCity(req.getParameter("city"));
            submittedAddress.setState(req.getParameter("state"));
            submittedAddress.setPostalCode(req.getParameter("postalCode"));
            submittedAddress.setCountry(req.getParameter("country"));
            req.setAttribute("address", submittedAddress);
            req.getRequestDispatcher("/WEB-INF/views/common/add-address.jsp").forward(req, resp);
            e.printStackTrace();
        }
    }
}
