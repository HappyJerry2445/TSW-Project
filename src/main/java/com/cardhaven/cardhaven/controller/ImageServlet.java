package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.ImageDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

@WebServlet("/image/*")
public class ImageServlet extends HttpServlet {

    // Cache duration: 1 day in seconds
    private static final long CACHE_AGE_SECONDS = 86400L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // Validate path and extract ID string
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image ID is required.");
            return;
        }

        String imageIdStr = pathInfo.substring(1);
        int imageID;
        try {
            imageID = Integer.parseInt(imageIdStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid image ID format.");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        var imageDAO = new ImageDAO(ds);

        try {
            // The 'image' variable will be of the type returned by getById()
            var image = imageDAO.getById(imageID);

            // Check if the image or its data exists
            if (image == null || image.getImageData() == null || image.getImageData().length == 0) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            byte[] imageData = image.getImageData();

            // --- Performance Improvement: Set cache headers ---
            resp.setHeader("Cache-Control", "public, max-age=" + CACHE_AGE_SECONDS);
            resp.setDateHeader("Expires", System.currentTimeMillis() + CACHE_AGE_SECONDS * 1000);

            // --- Set content type and length ---
            resp.setContentType(image.getMimeType());
            resp.setContentLength(imageData.length);

            // --- Write image data to response ---
            try (OutputStream out = resp.getOutputStream()) {
                out.write(imageData);
            }

        } catch (SQLException e) {
            // Log the exception and send a generic server error
            getServletContext().log("Database error retrieving image with ID: " + imageID, e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}