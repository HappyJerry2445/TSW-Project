package com.cardhaven.cardhaven.controller;


import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("doPost");

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        System.out.println("email: " + email);

        List<String> errors = new ArrayList<>();
        RequestDispatcher dispatcherToLoginPage = request.getRequestDispatcher("/WEB-INF/views/login.jsp");

        if (email == null || email.trim().isEmpty()) {
            errors.add("Il campo email non può essere vuoto");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("Il campo password non può essere vuoto");
        }
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            dispatcherToLoginPage.forward(request, response);
            return;
        }

        email = email.trim();
        password = password.trim();

        var userDao = new UserDAO((DataSource) getServletContext().getAttribute("ds"));
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
        session.setAttribute("loggedInUser", user);
        NotificationUtil.sendNotification(request, "Login effetuato con successo", "info");
        try {
            userDao.save(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var dispatcher = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(req, resp);
    }
}
