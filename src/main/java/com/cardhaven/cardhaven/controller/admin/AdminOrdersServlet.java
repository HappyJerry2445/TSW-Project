package com.cardhaven.cardhaven.controller.admin;

import com.cardhaven.cardhaven.model.dao.OrderAddressDAO;
import com.cardhaven.cardhaven.model.dao.OrderDAO;
import com.cardhaven.cardhaven.model.dao.UserDAO;
import com.cardhaven.cardhaven.model.dto.OrderAddressDTO;
import com.cardhaven.cardhaven.model.dto.OrderDTO;
import com.cardhaven.cardhaven.model.dto.UserDTO;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet(name = "AdminOrdersServlet", value = "/admin/orders")
public class AdminOrdersServlet extends HttpServlet {

    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private OrderAddressDAO orderAddressDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        DataSource dataSource = (DataSource) getServletContext().getAttribute("ds");
        orderDAO = new OrderDAO(dataSource);
        userDAO = new UserDAO(dataSource);
        orderAddressDAO = new OrderAddressDAO(dataSource);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<String> errors = new ArrayList<>();
        String startDateStr = request.getParameter("startDate");
        String endDateStr = request.getParameter("endDate");
        String userEmailFilter = request.getParameter("orderUserEmail");

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        Integer userIdFilter = null;

        try {
            if (startDateStr != null && !startDateStr.isEmpty()) {
                startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
                request.setAttribute("startDate", startDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
                request.setAttribute("endDate", endDateStr);
            }

            if (userEmailFilter != null && !userEmailFilter.isEmpty()) {
                UserDTO user = userDAO.getUserByEmail(userEmailFilter);
                if (user != null) {
                    userIdFilter = user.getId();
                    request.setAttribute("orderUserEmail", userEmailFilter);
                } else {
                    errors.add("Utente con email '" + userEmailFilter + "' non trovato. Il filtro utente non è stato applicato.");
                }
            }
            Collection<OrderDTO> orders = orderDAO.getFilteredOrders("OrderDate", true, startDate, endDate, userIdFilter);
            Map<Integer, UserDTO> userMap = new HashMap<>();
            Map<Integer, OrderAddressDTO> shippingAddressMap = new HashMap<>();
            Map<Integer, OrderAddressDTO> billingAddressMap = new HashMap<>();

            for (OrderDTO order : orders) {
                if (!userMap.containsKey(order.getUserID())) {
                    userMap.put(order.getUserID(), userDAO.getById(order.getUserID()));
                }
                if (!shippingAddressMap.containsKey(order.getShippingAddressId())) {
                    shippingAddressMap.put(order.getShippingAddressId(), orderAddressDAO.getById(order.getShippingAddressId()));
                }
                if (!billingAddressMap.containsKey(order.getBillingAddressId())) {
                    billingAddressMap.put(order.getBillingAddressId(), orderAddressDAO.getById(order.getBillingAddressId()));
                }
            }

            request.setAttribute("orders", orders);
            request.setAttribute("userMap", userMap);
            request.setAttribute("shippingAddressMap", shippingAddressMap);
            request.setAttribute("billingAddressMap", billingAddressMap);
            request.setAttribute("orderStatuses", OrderDTO.OrderStatus.values());
            request.setAttribute("errors", errors);

            request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);

        } catch (DateTimeParseException e) {
            errors.add("Formato data non valido. Deve essere nel formato 'yyyy-MM-dd'.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);
        } catch (SQLException e) {
            errors.add("Errore del database durante il recupero degli ordini.");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/admin/orders.jsp").forward(request, response);
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            NotificationUtil.sendNotification(request, "Azione non specificata.", "error");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(request.getParameter("orderId"));
        } catch (NumberFormatException e) {
            NotificationUtil.sendNotification(request, "ID ordine non valido.", "error");
            response.sendRedirect(request.getContextPath() + "/admin/orders");
            return;
        }

        try {
            OrderDTO order = orderDAO.getById(orderId);
            if (order == null) {
                NotificationUtil.sendNotification(request, "Ordine non trovato.", "error");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }

            switch (action) {
                case "updateStatus":
                    String newStatus = request.getParameter("newStatus");
                    if (newStatus == null || newStatus.isEmpty()) {
                        NotificationUtil.sendNotification(request, "Stato ordine non specificato.", "error");
                    } else {
                        order.setOrderStatus(OrderDTO.OrderStatus.valueOf(newStatus));
                        orderDAO.save(order);
                        NotificationUtil.sendNotification(request, "Stato ordine aggiornato con successo!", "success");
                    }
                    break;
                case "delete":
                    boolean deleted = orderDAO.delete(orderId);
                    if (deleted) {
                        NotificationUtil.sendNotification(request, "Ordine eliminato con successo.", "success");
                    } else {
                        NotificationUtil.sendNotification(request, "Impossibile eliminare l'ordine.", "error");
                    }
                    break;
                default:
                    NotificationUtil.sendNotification(request, "Azione non valida.", "error");
                    break;
            }

        } catch (SQLException e) {
            NotificationUtil.sendNotification(request, "Errore del database: " + e.getMessage(), "error");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            NotificationUtil.sendNotification(request, "Stato ordine non valido: " + e.getMessage(), "error");
        }
        response.sendRedirect(request.getContextPath() + "/admin/orders" + getFilterQueryString(request));
    }

    private String getFilterQueryString(HttpServletRequest request) {
        StringBuilder queryString = new StringBuilder("?");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String userEmail = request.getParameter("orderUserEmail");

        if (startDate != null && !startDate.isEmpty()) {
            queryString.append("startDate=").append(startDate).append("&");
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryString.append("endDate=").append(endDate).append("&");
        }
        if (userEmail != null && !userEmail.isEmpty()) {
            queryString.append("orderUserEmail=").append(userEmail).append("&");
        }
        if (queryString.length() > 1) { // Remove trailing '&' or '?' if no params
            return queryString.substring(0, queryString.length() - 1);
        }
        return "";
    }
}

