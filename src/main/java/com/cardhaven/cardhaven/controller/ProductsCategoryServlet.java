package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.CategoryDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.CategoryDTO;
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
import java.util.stream.Collectors;
import javax.sql.DataSource;

@WebServlet("/products/category/*")
public class ProductsCategoryServlet extends HttpServlet {

    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        System.out.println("doGet");

        String pathInfo = request.getPathInfo();
        List<String> errors = new ArrayList<>();
        request.setAttribute("errors", errors);

        if (pathInfo == null || pathInfo.equals("/")) {
            NotificationUtil.sendNotification(
                request,
                "Categoria non specificata.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        String IdStr = pathInfo.substring(1);
        int categoryId;
        try {
            categoryId = Integer.parseInt(IdStr);
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(
                request,
                "ID non valido",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        if (ds == null) {
            errors.add("Errore di configurazione del server.");
            request
                .getRequestDispatcher(
                    "/WEB-INF/views/ricerca-prodotti-categoria.jsp"
                )
                .forward(request, response);
            return;
        }

        ProductDAO productDAO = new ProductDAO(ds);
        CategoryDAO categoryDAO = new CategoryDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);

        try {
            CategoryDTO category = categoryDAO.getById(categoryId);
            if (category == null) {
                NotificationUtil.sendNotification(
                    request,
                    "Categoria non trovata.",
                    "error"
                );
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            Collection<ProductDTO> products = productDAO.getProductsByCategory(
                categoryId
            );
            Map<Integer, ProductImageDTO> productImages = new HashMap<>();

            // Create a Set of on-sale product IDs for quick lookup
            Set<Integer> onSaleProductIds = productDAO
                .findOnSale(Integer.MAX_VALUE)
                .stream()
                .map(ProductDTO::getProductId)
                .collect(Collectors.toSet());

            for (ProductDTO product : products) {
                ProductImageDTO image = productImageDAO.getFirstByProductId(
                    product.getProductId()
                );
                if (image != null) {
                    productImages.put(product.getProductId(), image);
                }
            }

            request.setAttribute("products", products);
            request.setAttribute("categoryName", category.getName());
            request.setAttribute("productImages", productImages);
            request.setAttribute("onSaleProductIds", onSaleProductIds);
            request
                .getRequestDispatcher(
                    "/WEB-INF/views/ricerca-prodotti-categoria.jsp"
                )
                .forward(request, response);
        } catch (Exception e) {
            errors.add(
                "Errore durante il recupero dei prodotti della categoria."
            );
            request
                .getRequestDispatcher(
                    "/WEB-INF/views/ricerca-prodotti-categoria.jsp"
                )
                .forward(request, response);
        }
    }

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        doGet(request, response);
    }
}
