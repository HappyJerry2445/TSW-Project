package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.sql.DataSource;

@WebServlet(name = "AdminUsersServlet", value = "/admin/users")
public class AdminUsersServlet extends HttpServlet {

    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-Z'\\s-]{2,50}$"
    );
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute(
            "ds"
        );
        userDAO = new UserDAO(dataSource);
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();

        String firstNameFilter = request.getParameter("firstName");
        String lastNameFilter = request.getParameter("lastName");
        String emailFilter = request.getParameter("email");
        String roleFilterStr = request.getParameter("role");

        if (
            firstNameFilter != null &&
            !firstNameFilter.trim().isEmpty() &&
            !NAME_PATTERN.matcher(firstNameFilter).matches()
        ) {
            errors.add("Formato del nome non valido per il filtro.");
            firstNameFilter = "";
        }
        if (
            lastNameFilter != null &&
            !lastNameFilter.trim().isEmpty() &&
            !NAME_PATTERN.matcher(lastNameFilter).matches()
        ) {
            errors.add("Formato del cognome non valido per il filtro.");
            lastNameFilter = "";
        }
        if (
            emailFilter != null &&
            !emailFilter.trim().isEmpty() &&
            !EMAIL_PATTERN.matcher(emailFilter).matches()
        ) {
            errors.add("Formato email non valido per il filtro.");
            emailFilter = "";
        }

        UserDTO.Role roleFilter = null;
        if (roleFilterStr != null && !roleFilterStr.isEmpty()) {
            try {
                roleFilter = UserDTO.Role.valueOf(roleFilterStr);
            } catch (IllegalArgumentException e) {
                errors.add("Ruolo specificato non valido.");
            }
        }

        try {
            Collection<UserDTO> users = userDAO.getFilteredUsers(
                "LastName",
                false,
                firstNameFilter,
                lastNameFilter,
                emailFilter,
                roleFilter
            );

            request.setAttribute("users", users);
            request.setAttribute("userRoles", UserDTO.Role.values()); // For dropdown filter and role update

            // Preserve filter values for the form
            request.setAttribute("firstName", firstNameFilter);
            request.setAttribute("lastName", lastNameFilter);
            request.setAttribute("email", emailFilter);
            request.setAttribute("role", roleFilterStr); // Keep as string for JSP selected option

            request.setAttribute("errors", errors);
            request
                .getRequestDispatcher("/WEB-INF/views/admin/users.jsp")
                .forward(request, response);
        } catch (SQLException e) {
            errors.add(
                "Errore del database durante il recupero degli utenti: " +
                e.getMessage()
            );
            request.setAttribute("errors", errors);
            request
                .getRequestDispatcher("/WEB-INF/views/admin/users.jsp")
                .forward(request, response);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            NotificationUtil.sendNotification(
                request,
                "Azione non specificata.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(request.getParameter("userId"));
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(
                request,
                "ID utente non valido.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        try {
            UserDTO user = userDAO.getById(userId);
            if (user == null) {
                NotificationUtil.sendNotification(
                    request,
                    "Utente non trovato.",
                    "error"
                );
                response.sendRedirect(
                    request.getContextPath() + "/admin/users"
                );
                return;
            }

            switch (action) {
                case "updateRole":
                    String newRoleStr = request.getParameter("newRole");
                    if (newRoleStr == null || newRoleStr.isEmpty()) {
                        NotificationUtil.sendNotification(
                            request,
                            "Ruolo non specificato.",
                            "error"
                        );
                    } else {
                        UserDTO.Role newRole = UserDTO.Role.valueOf(newRoleStr);
                        user.setRole(newRole);
                        userDAO.save(user);
                        NotificationUtil.sendNotification(
                            request,
                            "Ruolo utente aggiornato con successo!",
                            "success"
                        );
                    }
                    break;
                case "delete":
                    // Prevent deleting the currently logged-in admin user
                    Integer loggedInUserId = (Integer) request
                        .getSession()
                        .getAttribute("userId");
                    if (
                        loggedInUserId != null && loggedInUserId.equals(userId)
                    ) {
                        NotificationUtil.sendNotification(
                            request,
                            "Non puoi eliminare il tuo stesso account amministratore.",
                            "error"
                        );
                    } else {
                        boolean deleted = userDAO.delete(userId);
                        if (deleted) {
                            NotificationUtil.sendNotification(
                                request,
                                "Utente eliminato con successo.",
                                "success"
                            );
                        } else {
                            NotificationUtil.sendNotification(
                                request,
                                "Impossibile eliminare l'utente.",
                                "error"
                            );
                        }
                    }
                    break;
                default:
                    NotificationUtil.sendNotification(
                        request,
                        "Azione non valida.",
                        "error"
                    );
                    break;
            }
        } catch (SQLException e) {
            NotificationUtil.sendNotification(
                request,
                "Errore del database: " + e.getMessage(),
                "error"
            );
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            NotificationUtil.sendNotification(
                request,
                "Ruolo non valido: " + e.getMessage(),
                "error"
            );
        }
        // Redirect back to GET request to show updated list with filters preserved
        response.sendRedirect(
            request.getContextPath() +
            "/admin/users" +
            getFilterQueryString(request)
        );
    }

    // Helper to preserve filters on redirect
    private String getFilterQueryString(HttpServletRequest request) {
        StringBuilder queryString = new StringBuilder("?");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String role = request.getParameter("role");

        if (firstName != null && !firstName.isEmpty()) {
            queryString.append("firstName=").append(firstName).append("&");
        }
        if (lastName != null && !lastName.isEmpty()) {
            queryString.append("lastName=").append(lastName).append("&");
        }
        if (email != null && !email.isEmpty()) {
            queryString.append("email=").append(email).append("&");
        }
        if (role != null && !role.isEmpty()) {
            queryString.append("role=").append(role).append("&");
        }
        if (queryString.length() > 1) {
            // Remove trailing '&' or '?' if no params
            return queryString.substring(0, queryString.length() - 1);
        }
        return "";
    }
}
