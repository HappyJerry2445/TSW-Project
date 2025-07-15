package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.*;
import com.cardhaven.cardhaven.model.dto.*;
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
import java.util.*;

@WebServlet("/admin/orders/*")
public class AdminOrderDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        List<String> errors = new ArrayList<>();


        String pathInfo = request.getPathInfo(); // /{orderId}
        if (pathInfo == null || pathInfo.equals("/")) {
            NotificationUtil.sendNotification(request, "ID ordine mancante.", "error");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "ID ordine non valido.", "error");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        OrderDAO orderDAO = new OrderDAO(ds);
        OrderItemDAO orderItemDAO = new OrderItemDAO(ds);
        ProductDAO productDAO = new ProductDAO(ds);
        OrderAddressDAO addressDAO = new OrderAddressDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds); // Instantiate ProductImageDAO


        try {
            OrderDTO order = orderDAO.getById(orderId);

            // Recupera gli items dell'ordine
            List<OrderItemDTO> items = orderItemDAO.getOrderItemsByOrderId(orderId);

            // Recupera i dettagli dei prodotti per ogni item
            Map<Integer, ProductDTO> productMap = new HashMap<>();
            for (OrderItemDTO item : items) {
                if (!productMap.containsKey(item.getProductID())) {
                    ProductDTO product = productDAO.getById(item.getProductID());
                    if (product != null) {
                        productMap.put(item.getProductID(), product);
                    }
                }
            }

            Collection<ProductDTO> products = productDAO.getAll("ProductName");

            Map<Integer, Integer> productFirstImageIds = new HashMap<>();
            for (ProductDTO product : products) {
                ProductImageDTO firstImage = productImageDAO.getFirstByProductId(product.getProductId());
                if (firstImage != null) {
                    productFirstImageIds.put(product.getProductId(), firstImage.getImageId());
                }
            }

            // Recupera l'indirizzo di spedizione
            OrderAddressDTO shippingAddress = null;
            if (order.getShippingAddressId() > 0) {
                shippingAddress = addressDAO.getById(order.getShippingAddressId());
            }

            request.setAttribute("order", order);
            request.setAttribute("orderItems", items);
            request.setAttribute("productMap", productMap);
            request.setAttribute("shippingAddress", shippingAddress);
            request.setAttribute("productImages", productFirstImageIds);
            request.getRequestDispatcher("/WEB-INF/views/admin/order_details.jsp").forward(request, response);

        } catch (SQLException ex) {
            errors.add("Errore nel recupero dei dettagli dell'ordine.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/admin/order_details.jsp").forward(request, response);
            ex.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}