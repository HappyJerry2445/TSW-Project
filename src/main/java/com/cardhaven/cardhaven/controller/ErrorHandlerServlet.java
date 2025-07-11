package com.cardhaven.cardhaven.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/error")
public class ErrorHandlerServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processError(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processError(req, resp);
    }

    private void processError(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // Analyze the servlet request to get error details
        Throwable throwable = (Throwable) req.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Integer statusCode = (Integer) req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String servletName = (String) req.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);
        String requestUri = (String) req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String errorMessage = (String) req.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (statusCode == null && req.getParameter("code") != null) {
            try {
                statusCode = Integer.parseInt(req.getParameter("code"));
            } catch (NumberFormatException e) {
            }
        }
        if (statusCode == null) {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
        if (requestUri == null) {
            requestUri = "Unknown URI";
        }
        if (errorMessage == null) {
            errorMessage = "An unexpected error occurred";
        }

        // Log the error for internal debugging (crucial for centralized handling)
        if (throwable != null) {
            System.err.println("--- Error Details (Exception) ---"); // Use a proper logger in production
            System.err.println("Exception Type: " + throwable.getClass().getName());
            System.err.println("Error Message: " + throwable.getMessage());
            System.err.println("Request URI: " + requestUri);
            if (servletName != null) {
                System.err.println("Servlet Name: " + servletName);
            }
            throwable.printStackTrace(System.err); // Log stack trace
            System.err.println("---------------------------------");
        } else {
            System.err.println("--- Error Details (Status Code) ---"); // Use a proper logger in production
            System.err.println("Status Code: " + statusCode);
            System.err.println("Error Message: " + errorMessage);
            System.err.println("Request URI: " + requestUri);
            if (servletName != null) {
                System.err.println("Servlet Name: " + servletName);
            }
            System.err.println("-------------------------------------");
        }

        req.setAttribute("statusCode", statusCode);
        req.setAttribute("errorMessage", errorMessage);
        req.setAttribute("requestUri", requestUri);

        if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
            req.setAttribute("userMessage", "La pagina che cerchi non è stata trovata.");
        } else {
            req.setAttribute("userMessage", "Si è verificato un errore inaspettato. Per favore, riprova più tardi.");
        }
        req.getRequestDispatcher("/WEB-INF/views/error/error_page.jsp").forward(req, resp);
    }
}
