package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
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
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/common/profile")
public class ProfileServlet extends HttpServlet {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var userDAO = new UserDAO((DataSource) getServletContext().getAttribute("ds"));
        var session = req.getSession();

        var userId = (Integer) session.getAttribute("userId");
        UserDTO loggedInUser;
        try {
            loggedInUser = userDAO.getById(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        req.setAttribute("loggedInUser", loggedInUser);
        req.getRequestDispatcher("/WEB-INF/views/common/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var userDAO = new UserDAO((DataSource) getServletContext().getAttribute("ds"));
        var session = req.getSession();

        var userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();
        var firstName = req.getParameter("firstName");
        var lastName = req.getParameter("lastName");
        var email = req.getParameter("email");

        if (firstName == null || firstName.trim().isEmpty()) {
            errors.add("Il nome non può essere vuoto.");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            errors.add("Il cognome non può essere vuoto.");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("L'email non può essere vuota.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Il formato dell'email non è valido.");
        } else {
            // Controllo se l'email esiste già per un altro utente
            try {
                UserDTO existingUser = userDAO.getUserByEmail(email);
                // Se esiste un utente con questa email E il suo ID non è quello dell'utente corrente
                if (existingUser != null && existingUser.getId() != userId) {
                    errors.add("Questa email è già associata ad un altro account.");
                }
            } catch (SQLException e) {
                errors.add("Errore durante la verifica dell'email.");
                e.printStackTrace();
            }
        }
        // Se ci sono errori, ripresenta il form con i messaggi di errore
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            // Metti i dati sottomessi nel request per ripopolare il form
            /*
            TODO: Fai per bene
            req.setAttribute("submittedFirstName", firstName);
            req.setAttribute("submittedLastName", lastName);
            req.setAttribute("submittedEmail", email);
            req.setAttribute("initialEditMode", true);

            */
            req.getRequestDispatcher("/WEB-INF/views/common/profile.jsp").forward(req, resp);
            return;
        }

        UserDTO loggedInUser;
        try {
            loggedInUser = userDAO.getById(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // Aggiorna l'oggetto UserDTO con i nuovi dati
        loggedInUser.setFirstName(firstName);
        loggedInUser.setLastName(lastName);
        loggedInUser.setEmail(email);

        try {
            userDAO.save(loggedInUser);
            session.setAttribute("loggedInUser", loggedInUser);
            NotificationUtil.sendNotification(req, "Profilo aggiornato con successo!", "success");
            resp.sendRedirect(req.getContextPath() + "/common/profile");
        } catch (SQLException e) {
            errors.add("Errore durante l'aggiornamento del profilo.");
            req.setAttribute("errors", errors);
            req.setAttribute("loggedInUser", loggedInUser);
            /*
            TODO: Fai per bene
            req.setAttribute("submittedFirstName", firstName);
            req.setAttribute("submittedLastName", lastName);
            req.setAttribute("submittedEmail", email);
            req.setAttribute("initialEditMode", true);
             */
            req.getRequestDispatcher("/WEB-INF/views/common/profile.jsp").forward(req, resp);
            e.printStackTrace();
        }
    }
}
