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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.sql.DataSource;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final String EMAIL_REGEX =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final String NAME_REGEX = "^[A-Za-zÀ-ÖØ-öø-ÿ'\\- ]{2,50}$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final String PASSWORD_REGEX =
        "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        PASSWORD_REGEX
    );

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        List<String> errors = new ArrayList<>();
        RequestDispatcher dispatcher = req.getRequestDispatcher(
            "/WEB-INF/views/register.jsp"
        );

        // --- Validation ---
        if (firstName == null || firstName.trim().isEmpty()) {
            errors.add("Il campo nome non può essere vuoto.");
        } else if (!NAME_PATTERN.matcher(firstName.trim()).matches()) {
            errors.add(
                "Il nome contiene caratteri non validi o è troppo corto/lungo."
            );
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            errors.add("Il campo cognome non può essere vuoto.");
        } else if (!NAME_PATTERN.matcher(lastName.trim()).matches()) {
            errors.add(
                "Il cognome contiene caratteri non validi o è troppo corto/lungo."
            );
        }

        if (email == null || email.trim().isEmpty()) {
            errors.add("Il campo email non può essere vuoto.");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Il formato dell'email non è valido.");
        }

        if (password == null || password.isEmpty()) {
            errors.add("Il campo password non può essere vuoto.");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errors.add(
                "La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola, un numero e un carattere speciale."
            );
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errors.add("Il campo conferma password non può essere vuoto.");
        }

        // Check for password mismatch only if other validation passes for password fields
        if (errors.isEmpty() && !password.equals(confirmPassword)) {
            errors.add("Le password non corrispondono.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            dispatcher.forward(req, resp);
            return;
        }

        UserDAO userDAO = new UserDAO(
            (DataSource) getServletContext().getAttribute("ds")
        );

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
            UserDTO newUser = new UserDTO(
                firstName,
                lastName,
                email,
                hashedPassword
            );
            userDAO.save(newUser);
            var session = req.getSession();
            UserLoginUtil.login(session, newUser);
            NotificationUtil.sendNotification(
                req,
                "Registrazione effetuata con successo",
                "info"
            );
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (SQLException e) {
            errors.add(
                "Errore inaspettato durante la registrazione dell'utente"
            );
            req.setAttribute("errors", errors);
            dispatcher.forward(req, resp);
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        HttpSession session = req.getSession(false); // Get existing session, don't create a new one
        if (session != null && session.getAttribute("userId") != null) {
            // Check for the attribute you set on login
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        var dispatcher = req.getRequestDispatcher(
            "/WEB-INF/views/register.jsp"
        );
        dispatcher.forward(req, resp);
    }
}
