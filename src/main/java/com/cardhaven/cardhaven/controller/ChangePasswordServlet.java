package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.UserDAO;
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
import java.util.Objects;
import java.util.regex.Pattern;
import javax.sql.DataSource;

@WebServlet("/common/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private static final String PASSWORD_REGEX =
        "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        PASSWORD_REGEX
    );

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        // Just forward to the JSP for displaying the form
        req
            .getRequestDispatcher("/WEB-INF/views/common/change-password.jsp")
            .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        HttpSession session = req.getSession();
        UserDAO userDAO = new UserDAO(
            (DataSource) getServletContext().getAttribute("ds")
        );
        var userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        UserDTO loggedInUser;
        try {
            loggedInUser = userDAO.getById(userId);
        } catch (SQLException e) {
            errors.add("Si è verificato un errore inaspettato");
            req.setAttribute("errors", errors);
            req
                .getRequestDispatcher(
                    "/WEB-INF/views/common/change-password.jsp"
                )
                .forward(req, resp);
            return;
        }
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmNewPassword = req.getParameter("confirmNewPassword");

        // --- Validation ---
        if (currentPassword == null || currentPassword.isEmpty()) {
            errors.add("Il campo password attuale è obbligatorio.");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            errors.add("Il campo nuova password è obbligatorio.");
        } else if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            errors.add(
                "La nuova password non è sicura. Deve contenere almeno una maiuscola, una minuscola, un numero e un carattere speciale."
            );
        }

        if (confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            errors.add("Il campo di conferma password è obbligatorio.");
        }

        // Check for mismatch only if other validation on password fields is likely to pass
        if (errors.isEmpty() && !newPassword.equals(confirmNewPassword)) {
            errors.add("La nuova password e la conferma non corrispondono.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req
                .getRequestDispatcher(
                    "/WEB-INF/views/common/change-password.jsp"
                )
                .forward(req, resp);
            return;
        }

        try {
            // Verify current password
            // Retrieve the user from DB to get the hashed password
            UserDTO userFromDb = userDAO.getById(loggedInUser.getId());
            if (
                userFromDb == null ||
                !userDAO.verifyPassword(
                    currentPassword,
                    userFromDb.getPasswordHash()
                )
            ) {
                errors.add("La password attuale non è corretta.");
                req.setAttribute("errors", errors);
                req
                    .getRequestDispatcher(
                        "/WEB-INF/views/common/change-password.jsp"
                    )
                    .forward(req, resp);
                return;
            }

            // Hash the new password
            String newHashedPassword = userDAO.hashPassword(newPassword);

            // Update password in the database
            userFromDb.setPasswordHash(newHashedPassword);
            userDAO.save(userFromDb); // Assuming save method updates if ID exists

            NotificationUtil.sendNotification(
                req,
                "Password aggiornata con successo!",
                "success"
            );
            resp.sendRedirect(req.getContextPath() + "/common/profile");
        } catch (SQLException e) {
            errors.add("Errore durante l'aggiornamento della password.");
            req.setAttribute("errors", errors);
            req
                .getRequestDispatcher(
                    "/WEB-INF/views/common/change-password.jsp"
                )
                .forward(req, resp);
            e.printStackTrace();
        }
    }
}
