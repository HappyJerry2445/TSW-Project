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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.sql.DataSource;

@WebServlet("/common/addresses/edit")
public class EditAddressServlet extends HttpServlet {

    // Regex for validation
    private static final String STREET_REGEX =
        "^[a-zA-Z0-9À-ÖØ-öø-ÿ'\\s.,-]{5,255}$";
    private static final String CITY_COUNTRY_REGEX =
        "^[a-zA-ZÀ-ÖØ-öø-ÿ'\\s-]{2,100}$";
    private static final String STATE_REGEX = "^[A-Z]{2}$"; // Assuming 2-letter state codes like IT provinces
    private static final String POSTAL_CODE_REGEX = "^[0-9]{5}$"; // Italian CAP format

    private static final Pattern STREET_PATTERN = Pattern.compile(STREET_REGEX);
    private static final Pattern CITY_COUNTRY_PATTERN = Pattern.compile(
        CITY_COUNTRY_REGEX
    );
    private static final Pattern STATE_PATTERN = Pattern.compile(STATE_REGEX);
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile(
        POSTAL_CODE_REGEX
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        DataSource dataSource = (DataSource) getServletContext().getAttribute(
            "ds"
        );
        UserDAO userDAO = new UserDAO(dataSource);
        AddressDAO addressDAO = new AddressDAO(dataSource);

        try {
            UserDTO loggedInUser = userDAO.getById(userId);
            if (loggedInUser == null) {
                NotificationUtil.sendNotification(
                    req,
                    "Utente non trovato. Si prega di accedere nuovamente.",
                    "error"
                );
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String addressIdParam = req.getParameter("addressId");
            if (addressIdParam == null || addressIdParam.isEmpty()) {
                errors.add("ID indirizzo non specificato per la modifica.");
                req.setAttribute("errors", errors);
                req
                    .getRequestDispatcher("/WEB-INF/views/common/addresses.jsp")
                    .forward(req, resp); // Go back to addresses page
                return;
            }

            int addressId = Integer.parseInt(addressIdParam);
            AddressDTO address = addressDAO.getById(addressId);

            if (
                address == null || address.getUserID() != loggedInUser.getId()
            ) {
                errors.add(
                    "Indirizzo non trovato o non autorizzato per la modifica."
                );
                req.setAttribute("errors", errors);
                req
                    .getRequestDispatcher("/WEB-INF/views/common/addresses.jsp")
                    .forward(req, resp);
                return;
            }

            req.setAttribute("addressTypes", AddressDTO.AddressType.values());
            req.setAttribute("address", address);
            req
                .getRequestDispatcher("/WEB-INF/views/common/edit-address.jsp")
                .forward(req, resp);
        } catch (NumberFormatException e) {
            errors.add("ID indirizzo non valido.");
            req.setAttribute("errors", errors);
            req
                .getRequestDispatcher("/WEB-INF/views/common/addresses.jsp")
                .forward(req, resp);
        } catch (SQLException e) {
            errors.add(
                "Errore del database durante il recupero dell'indirizzo."
            );
            req.setAttribute("errors", errors);
            req
                .getRequestDispatcher("/WEB-INF/views/common/addresses.jsp")
                .forward(req, resp);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        DataSource dataSource = (DataSource) getServletContext().getAttribute(
            "ds"
        );
        UserDAO userDAO = new UserDAO(dataSource);
        AddressDAO addressDAO = new AddressDAO(dataSource);

        try {
            UserDTO loggedInUser = userDAO.getById(userId);
            if (loggedInUser == null) {
                NotificationUtil.sendNotification(
                    req,
                    "Utente non trovato. Si prega di accedere nuovamente.",
                    "error"
                );
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String addressIdParam = req.getParameter("id");
            String street = req.getParameter("street");
            String city = req.getParameter("city");
            String state = req.getParameter("state");
            String postalCode = req.getParameter("postalCode");
            String country = req.getParameter("country");
            String addressTypeStr = req.getParameter("addressType");

            // --- Detailed Validation ---
            if (addressIdParam == null || addressIdParam.trim().isEmpty()) {
                errors.add("ID Indirizzo mancante.");
            }
            if (street == null || !STREET_PATTERN.matcher(street).matches()) {
                errors.add(
                    "L'indirizzo non è valido (min 5, max 255 caratteri)."
                );
            }
            if (city == null || !CITY_COUNTRY_PATTERN.matcher(city).matches()) {
                errors.add(
                    "La città può contenere solo lettere, spazi, e trattini."
                );
            }
            if (
                state != null &&
                !state.trim().isEmpty() &&
                !STATE_PATTERN.matcher(state).matches()
            ) {
                errors.add(
                    "La provincia deve essere composta da due lettere maiuscole (es. SA)."
                );
            }
            if (
                postalCode == null ||
                !POSTAL_CODE_PATTERN.matcher(postalCode).matches()
            ) {
                errors.add("Il CAP deve essere composto da 5 cifre numeriche.");
            }
            if (
                country == null ||
                !CITY_COUNTRY_PATTERN.matcher(country).matches()
            ) {
                errors.add(
                    "La nazione può contenere solo lettere, spazi, e trattini."
                );
            }
            if (addressTypeStr == null || addressTypeStr.isEmpty()) {
                errors.add("È necessario selezionare un tipo di indirizzo.");
            }

            int addressId = 0;
            try {
                addressId = Integer.parseInt(addressIdParam);
            } catch (NumberFormatException e) {
                errors.add("ID indirizzo non valido.");
            }

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
                submittedAddress.setAddressID(addressId);
                submittedAddress.setStreetAddress(street);
                submittedAddress.setCity(city);
                submittedAddress.setState(state);
                submittedAddress.setPostalCode(postalCode);
                submittedAddress.setCountry(country);
                req.setAttribute("address", submittedAddress); // Pass submitted data back to form
                req
                    .getRequestDispatcher(
                        "/WEB-INF/views/common/edit-address.jsp"
                    )
                    .forward(req, resp);
                return;
            }

            // Security check: Ensure the address being edited belongs to the logged-in user
            AddressDTO existingAddress = addressDAO.getById(addressId);
            if (
                existingAddress == null ||
                existingAddress.getUserID() != loggedInUser.getId()
            ) {
                NotificationUtil.sendNotification(
                    req,
                    "Tentativo di modifica di un indirizzo non autorizzato.",
                    "error"
                );
                resp.sendRedirect(req.getContextPath() + "/common/addresses");
                return;
            }

            // Create an Address object with updated values
            AddressDTO updatedAddress = new AddressDTO();
            updatedAddress.setAddressID(addressId);
            updatedAddress.setUserID(loggedInUser.getId()); // Set user ID for the update query's WHERE clause
            updatedAddress.setStreetAddress(street);
            updatedAddress.setCity(city);
            updatedAddress.setState(state);
            updatedAddress.setPostalCode(postalCode);
            updatedAddress.setCountry(country);
            updatedAddress.setAddressType(addressType);

            addressDAO.save(updatedAddress);

            NotificationUtil.sendNotification(
                req,
                "Indirizzo aggiornato con successo!",
                "success"
            );
            resp.sendRedirect(req.getContextPath() + "/common/addresses"); // Redirect to address list
        } catch (SQLException e) {
            errors.add(
                "Errore del database durante l'aggiornamento dell'indirizzo."
            );
            req.setAttribute("errors", errors);
            // Re-populate the address object with submitted values for display in case of DB error
            AddressDTO submittedAddress = new AddressDTO();
            try {
                submittedAddress.setAddressID(
                    Integer.parseInt(req.getParameter("id"))
                );
            } catch (NumberFormatException ex) {
                /* ignored, ID is already validated */
            }
            submittedAddress.setStreetAddress(req.getParameter("street"));
            submittedAddress.setCity(req.getParameter("city"));
            submittedAddress.setState(req.getParameter("state"));
            submittedAddress.setPostalCode(req.getParameter("postalCode"));
            submittedAddress.setCountry(req.getParameter("country"));
            req.setAttribute("address", submittedAddress);
            req
                .getRequestDispatcher("/WEB-INF/views/common/edit-address.jsp")
                .forward(req, resp);
            e.printStackTrace();
        }
    }
}
