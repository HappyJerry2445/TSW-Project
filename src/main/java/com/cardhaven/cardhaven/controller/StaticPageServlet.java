package com.cardhaven.cardhaven.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Set;

@WebServlet("/pages/*")
public class StaticPageServlet extends HttpServlet {

    // A whitelist of allowed static pages to prevent security vulnerabilities
    private static final Set<String> ALLOWED_PAGES = Set.of(
            "about",
            "contact",
            "faq",
            "terms",
            "privacy"
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        // If there's no path info or just a slash, it's an invalid request.
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Extract the page name from the path, e.g., "/about" -> "about"
        String pageName = pathInfo.substring(1).toLowerCase();

        // Check if the requested page is in our whitelist.
        if (ALLOWED_PAGES.contains(pageName)) {
            // If it's an allowed page, construct the path to the JSP file.
            String jspPath = "/WEB-INF/views/static/" + pageName + ".jsp";
            // Forward the request to the corresponding JSP page.
            request.getRequestDispatcher(jspPath).forward(request, response);
        } else {
            // If the page is not in the whitelist, send a 404 Not Found error.
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
