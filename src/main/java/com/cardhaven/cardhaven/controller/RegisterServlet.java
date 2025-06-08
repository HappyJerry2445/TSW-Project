package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import com.cardhaven.cardhaven.util.UserLoginUtil;
import jakarta.servlet.RequestDispatcher;
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
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        List<String> errors = new ArrayList<>();
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/register.jsp");

        if (firstName == null || firstName.trim().isEmpty()) {
            errors.add("Il campo nome non può essere vuoto");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            errors.add("Il campo cognome non può essere vuoto");
        }
        if (email == null || email.trim().isEmpty()) {
            errors.add("Il campo email non può essere vuoto");
        }
        if (email != null && !EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Email invalido");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("Il campo password non può essere vuoto");
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errors.add("Il campo conferma password non può essere vuoto");
        }
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            dispatcher.forward(req, resp);
            return;
        }

        if (!confirmPassword.equals(password)) {
            errors.add("Le password non corrispondono");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            dispatcher.forward(req, resp);
            return;
        }

        UserDAO userDAO = new UserDAO((DataSource) getServletContext().getAttribute("ds"));

        try {
            if (userDAO.getUserByEmail(email) != null) {
                errors.add("Questa email è già registrata");
            }
        } catch (SQLException e) {
            errors.add("Errore inaspettato durante la verifica dell'email");
            e.printStackTrace();
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            dispatcher.forward(req, resp);
            return;
        }
        try {
            String hashedPassword = userDAO.hashPassword(password);
            UserDTO newUser = new UserDTO(firstName, lastName, email, hashedPassword);
            userDAO.save(newUser);
            var session = req.getSession();
            UserLoginUtil.login(session, newUser);
            NotificationUtil.sendNotification(req, "Registrazione effetuata con successo", "info");
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (SQLException e) {
            errors.add("Errore inaspettato durante la registrazione dell'utente");
            req.setAttribute("errors", errors);
            dispatcher.forward(req, resp);
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false); // Get existing session, don't create a new one
        if (session != null && session.getAttribute("userId") != null) { // Check for the attribute you set on login
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        var dispatcher = req.getRequestDispatcher("/WEB-INF/views/register.jsp");
        dispatcher.forward(req, resp);
    }
}
