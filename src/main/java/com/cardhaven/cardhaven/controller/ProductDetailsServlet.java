package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.model.dto.ProductImageDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import javax.sql.DataSource;

@WebServlet("/products/detail/*")
public class ProductDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        System.out.println(
            "ProductServlet: doGet chiamato con pathInfo=" + req.getPathInfo()
        );

        String pathInfo = req.getPathInfo();
        List<String> errors = new ArrayList<>();
        req.setAttribute("errors", errors);

        if (pathInfo == null || pathInfo.equals("/")) {
            errors.add("Id del prodotto non specificato.");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        String productIdStr = pathInfo.substring(1);
        int productId;

        try {
            productId = Integer.parseInt(productIdStr);
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(
                req,
                "ID prodotto non valido.",
                "error"
            );
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        if (ds == null) {
            errors.add("Errore di configurazione del server.");
            req
                .getRequestDispatcher("/WEB-INF/views/product-details.jsp")
                .forward(req, resp);
            return;
        }

        ProductDAO productDAO = new ProductDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);

        try {
            ProductDTO product = productDAO.getById(productId);
            if (product == null) {
                NotificationUtil.sendNotification(
                    req,
                    "Prodotto non trovato.",
                    "error"
                );
                resp.sendRedirect(req.getContextPath() + "/");
                return;
            }

            ProductImageDTO image = productImageDAO.getFirstByProductId(
                productId
            );
            Map<Integer, ProductImageDTO> productImages = new HashMap<>();

            if (image != null) {
                productImages.put(productId, image);
            }

            req.setAttribute("product", product);
            req.setAttribute("productImages", productImages);
            req.setAttribute("pageTitle", product.getProductName());

            req
                .getRequestDispatcher("/WEB-INF/views/product-details.jsp")
                .forward(req, resp);
        } catch (Exception e) {
            errors.add("Errore durante il recupero dei dettagli del prodotto.");
            req
                .getRequestDispatcher("/WEB-INF/views/product-details.jsp")
                .forward(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
        doGet(req, resp);
    }
}
