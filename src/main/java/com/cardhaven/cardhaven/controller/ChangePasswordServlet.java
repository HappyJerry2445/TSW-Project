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

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@WebServlet("/common/change-password")
public class ChangePasswordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Just forward to the JSP for displaying the form
        req.getRequestDispatcher("/WEB-INF/views/common/change-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        UserDAO userDAO = new UserDAO((DataSource) getServletContext().getAttribute("ds"));
        var userId = (Integer) session.getAttribute("userId");
        List<String> errors = new ArrayList<>();

        UserDTO loggedInUser;
        try {
            loggedInUser = userDAO.getById(userId);
        } catch (SQLException e) {
            errors.add("Si è verificato un errore inaspettato");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/views/common/change-password.jsp").forward(req, resp);
            return;
        }
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmNewPassword = req.getParameter("confirmNewPassword");

        // Basic validation
        if (currentPassword == null || currentPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                confirmNewPassword == null || confirmNewPassword.isEmpty()) {
            errors.add("Tutti i campi sono obbligatori.");
        }

        // TODO Extract this segment and make an util class
        if (!Objects.equals(newPassword, confirmNewPassword)) {
            errors.add("La nuova password e la conferma della nuova password non corrispondono.");
        }

        // Password strength validation (example: min 8 characters)
        if ((newPassword != null ? newPassword.length() : 0) < 8) {
            errors.add("La nuova password deve essere lunga almeno 8 caratteri.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/views/common/change-password.jsp").forward(req, resp);
            return;
        }

        try {
            // Verify current password
            // Retrieve the user from DB to get the hashed password
            UserDTO userFromDb = userDAO.getById(loggedInUser.getId());
            if (userFromDb == null || !userDAO.verifyPassword(currentPassword, userFromDb.getPasswordHash())) {
                errors.add("La password attuale non è corretta.");
                req.setAttribute("errors", errors);
                req.getRequestDispatcher("/WEB-INF/views/common/change-password.jsp").forward(req, resp);
                return;
            }

            // Hash the new password
            String newHashedPassword = userDAO.hashPassword(newPassword);

            // Update password in the database
            userFromDb.setPasswordHash(newHashedPassword);
            userDAO.save(userFromDb); // Assuming save method updates if ID exists

            NotificationUtil.sendNotification(req, "Password aggiornata con successo!", "success");
            resp.sendRedirect(req.getContextPath() + "/common/profile");

        } catch (SQLException e) {
            errors.add("Errore durante l'aggiornamento della password.");
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/WEB-INF/views/common/change-password.jsp").forward(req, resp);
            e.printStackTrace();
        }
    }
}
