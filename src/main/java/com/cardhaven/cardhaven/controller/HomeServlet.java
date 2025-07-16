package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.ProductDTO;
import com.cardhaven.cardhaven.model.dto.ProductImageDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;

@WebServlet(name = "HomeServlet", value = "/home")
public class HomeServlet extends HttpServlet {

    /**
     * Handles GET requests to the homepage.
     * Fetches the newest products and products on sale to display on the main page.
     *
     * @param request  the HttpServletRequest object that contains the request the client has made of the servlet
     * @param response the HttpServletResponse object that contains the response the servlet sends to the client
     * @throws ServletException if the request for the GET could not be handled
     * @throws IOException      if an input or output error is detected when the servlet handles the GET request
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        ProductDAO productDAO = new ProductDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);

        try {
            // 1. Fetch lists of products: newest arrivals and products currently on sale.
            List<ProductDTO> newestProducts = productDAO.findTopN(8);
            List<ProductDTO> onSaleProducts = productDAO.findOnSale(8);

            // 2. Create a Set of on-sale product IDs for easy checking in the JSP.
            Set<Integer> onSaleProductIds = onSaleProducts
                .stream()
                .map(ProductDTO::getProductId)
                .collect(Collectors.toSet());

            // 3. Create maps to associate product IDs with their primary image IDs for efficient lookup in the JSP.
            Map<Integer, Integer> newestProductImages = new HashMap<>();
            for (ProductDTO product : newestProducts) {
                ProductImageDTO image = productImageDAO.getFirstByProductId(
                    product.getProductId()
                );
                if (image != null) {
                    newestProductImages.put(
                        product.getProductId(),
                        image.getImageId()
                    );
                }
            }

            Map<Integer, Integer> onSaleProductImages = new HashMap<>();
            for (ProductDTO product : onSaleProducts) {
                ProductImageDTO image = productImageDAO.getFirstByProductId(
                    product.getProductId()
                );
                if (image != null) {
                    onSaleProductImages.put(
                        product.getProductId(),
                        image.getImageId()
                    );
                }
            }

            // 4. Set the fetched data as request attributes to be accessed by the JSP.
            request.setAttribute("newestProducts", newestProducts);
            request.setAttribute("onSaleProducts", onSaleProducts);
            request.setAttribute("onSaleProductIds", onSaleProductIds);
            request.setAttribute("newestProductImages", newestProductImages);
            request.setAttribute("onSaleProductImages", onSaleProductImages);
        } catch (SQLException e) {
            // Robust exception handling: log the error and set an error message for the user.
            System.err.println(
                "SQL error fetching data for homepage: " + e.getMessage()
            );
            request.setAttribute(
                "errorMessage",
                "Unable to load products at the moment. Please try again later."
            );
            // Set empty collections to prevent NullPointerExceptions in the JSP.
            request.setAttribute("newestProducts", Collections.emptyList());
            request.setAttribute("onSaleProducts", Collections.emptyList());
        }

        // 4. Forward the request to the index.jsp view for rendering.
        RequestDispatcher dispatcher = request.getRequestDispatcher(
            "/index.jsp"
        );
        dispatcher.forward(request, response);
    }
}
