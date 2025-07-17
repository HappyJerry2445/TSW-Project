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
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.sql.DataSource;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final String EMAIL_REGEX =
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        System.out.println("doPost");
        if (request.getSession().getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("email: " + email);

        List<String> errors = new ArrayList<>();
        RequestDispatcher dispatcherToLoginPage = request.getRequestDispatcher(
            "/WEB-INF/views/login.jsp"
        );

        if (email == null || email.trim().isEmpty()) {
            errors.add("Il campo email non può essere vuoto");
        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("Il formato dell'email non è valido.");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.add("Il campo password non può essere vuoto");
        } else if (password.length() < 8) {
            errors.add("La password deve essere lunga almeno 8 caratteri.");
        }
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            dispatcherToLoginPage.forward(request, response);
            return;
        }

        email = email.trim();
        password = password.trim();

        var userDao = new UserDAO(
            (DataSource) getServletContext().getAttribute("ds")
        );
        UserDTO user = null;
        try {
            user = userDao.getUserByEmail(email);
        } catch (SQLException e) {
            errors.add("Errore inaspettato");
            request.setAttribute("errors", errors);
            dispatcherToLoginPage.forward(request, response);
            return;
        }
        if (user == null) {
            errors.add("Utente non trovato");
        } else if (!userDao.verifyPassword(password, user.getPasswordHash())) {
            errors.add("Password errata");
        }
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            dispatcherToLoginPage.forward(request, response);
            return;
        }
        var session = request.getSession();
        user.setLastLogin(LocalDateTime.now());
        UserLoginUtil.login(session, user);
        NotificationUtil.sendNotification(
            request,
            "Login effetuato con successo",
            "info"
        );
        try {
            userDao.save(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String redirectUrl = (String) session.getAttribute(
            "redirectAfterLogin"
        );
        if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
            session.removeAttribute("redirectAfterLogin"); // Remove the attribute after use
            response.sendRedirect(redirectUrl);
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        if (req.getSession().getAttribute("userId") != null) {
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        var dispatcher = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(req, resp);
    }
}
