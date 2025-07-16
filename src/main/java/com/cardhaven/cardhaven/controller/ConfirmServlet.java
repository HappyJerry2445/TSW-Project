package com.cardhaven.cardhaven.controller;

import com.cardhaven.cardhaven.model.dao.*;
import com.cardhaven.cardhaven.model.dto.*;
import com.cardhaven.cardhaven.util.CartManager;
import com.cardhaven.cardhaven.util.NotificationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import javax.sql.DataSource;

@WebServlet("/common/checkout/confirm")
public class ConfirmServlet extends HttpServlet {

    @Override
    protected void doPost(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession();
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");

        // Initialize DAOs
        OrderDAO orderDAO = new OrderDAO(ds);
        OrderItemDAO orderItemDAO = new OrderItemDAO(ds);
        CartDAO cartDAO = new CartDAO(ds);
        CartItemDAO cartItemDAO = new CartItemDAO(ds);
        ProductDAO productDAO = new ProductDAO(ds);
        ProductImageDAO productImageDAO = new ProductImageDAO(ds);
        OrderAddressDAO orderAddressDAO = new OrderAddressDAO(ds);
        AddressDAO addressDAO = new AddressDAO(ds);

        // Get session attributes
        Integer userId = (Integer) session.getAttribute("userId");
        Integer shippingAddressId = (Integer) session.getAttribute(
            "shippingAddressId"
        );
        Integer billingAddressId = (Integer) session.getAttribute(
            "billingAddressId"
        );

        // Validate user authentication
        if (userId == null) {
            NotificationUtil.sendNotification(
                request,
                "Devi essere loggato per confermare il tuo ordine.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Validate addresses (must come from checkout flow)
        if (shippingAddressId == null || billingAddressId == null) {
            NotificationUtil.sendNotification(
                request,
                "Indirizzi non selezionati. Riprova il checkout.",
                "error"
            );
            response.sendRedirect(
                request.getContextPath() + "/common/checkout/shipping"
            );
            return;
        }

        // Check if order was already processed (prevent duplicate orders)
        Boolean orderProcessed = (Boolean) session.getAttribute(
            "orderProcessed"
        );
        if (orderProcessed != null && orderProcessed) {
            NotificationUtil.sendNotification(
                request,
                "Ordine già processato.",
                "warning"
            );
            response.sendRedirect(
                request.getContextPath() + "/common/checkout/confirm"
            );
            return;
        }

        Connection conn = null;
        try {
            // Get connection and start transaction
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            // Get cart items
            List<CartItemDetailDTO> cartItems =
                CartManager.getDetailedCartItems(
                    request,
                    cartDAO,
                    cartItemDAO,
                    productDAO,
                    productImageDAO
                );
            if (cartItems.isEmpty()) {
                NotificationUtil.sendNotification(
                    request,
                    "Il tuo carrello è vuoto.",
                    "error"
                );
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            // --- VALIDATE STOCK AVAILABILITY FIRST ---
            for (CartItemDetailDTO cartItem : cartItems) {
                ProductDTO product = productDAO.getById(
                    cartItem.getProductId()
                );
                if (product == null) {
                    throw new SQLException(
                        "Prodotto non trovato: " + cartItem.getProductName()
                    );
                }
                if (product.getStockQuantity() < cartItem.getQuantity()) {
                    throw new SQLException(
                        "Scorte insufficienti per il prodotto: " +
                        cartItem.getProductName() +
                        " (Disponibili: " +
                        product.getStockQuantity() +
                        ", Richieste: " +
                        cartItem.getQuantity() +
                        ")"
                    );
                }
            }

            // --- CREATE ORDER ADDRESSES (SNAPSHOTS) ---
            OrderAddressDTO shippingOrderAddress = createOrderAddressSnapshot(
                addressDAO,
                orderAddressDAO,
                shippingAddressId,
                "spedizione"
            );
            OrderAddressDTO billingOrderAddress = createOrderAddressSnapshot(
                addressDAO,
                orderAddressDAO,
                billingAddressId,
                "fatturazione"
            );

            // --- CREATE ORDER ---
            BigDecimal total = calculateOrderTotal(cartItems);
            OrderDTO newOrder = createOrder(
                userId,
                shippingOrderAddress,
                billingOrderAddress,
                total
            );
            orderDAO.save(newOrder);

            // --- CREATE ORDER ITEMS AND UPDATE STOCK ---
            for (CartItemDetailDTO cartItem : cartItems) {
                processOrderItem(productDAO, orderItemDAO, newOrder, cartItem);
            }

            // --- CLEAR CART ---
            clearUserCart(cartDAO, cartItemDAO, userId);

            // --- COMMIT TRANSACTION ---
            conn.commit();

            // --- POST-PROCESSING ---
            session.setAttribute("lastOrderId", newOrder.getOrderID());
            session.setAttribute("orderProcessed", true); // Mark order as processed
            cleanupCheckoutSession(session);

            // Success notification and redirect
            NotificationUtil.sendNotification(
                request,
                "Ordine confermato con successo!",
                "success"
            );
            response.sendRedirect(
                request.getContextPath() + "/common/checkout/confirm"
            );
        } catch (SQLException e) {
            handleCheckoutError(conn, e, request, response);
        } finally {
            closeConnection(conn);
        }
    }

    @Override
    protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession();
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");

        UserDAO userDAO = new UserDAO(ds);
        OrderDAO orderDAO = new OrderDAO(ds);
        OrderItemDAO orderItemDAO = new OrderItemDAO(ds);

        Integer userId = (Integer) session.getAttribute("userId");
        Integer lastOrderId = (Integer) session.getAttribute("lastOrderId");

        // Check if user is logged in
        if (userId == null) {
            NotificationUtil.sendNotification(
                request,
                "Accesso negato. Effettua il login.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Check if user accessed this page through proper checkout flow
        Boolean orderProcessed = (Boolean) session.getAttribute(
            "orderProcessed"
        );
        if (
            lastOrderId == null || (orderProcessed == null || !orderProcessed)
        ) {
            NotificationUtil.sendNotification(
                request,
                "Accesso non autorizzato. Completa prima il checkout.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        try {
            // Get user information
            UserDTO loggedInUser = userDAO.getById(userId);
            request.setAttribute("loggedInUser", loggedInUser);

            // Get last order details
            OrderDTO lastOrder = orderDAO.getById(lastOrderId);
            if (lastOrder != null && lastOrder.getUserID() == userId) {
                request.setAttribute("lastOrder", lastOrder);

                // Get order items for display
                List<OrderItemDTO> orderItems =
                    orderItemDAO.getOrderItemsByOrderId(lastOrderId);
                request.setAttribute("orderItems", orderItems);
            } else {
                NotificationUtil.sendNotification(
                    request,
                    "Ordine non trovato.",
                    "error"
                );
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            // Clean up session after displaying order (prevent refresh issues)
            session.removeAttribute("lastOrderId");
            session.removeAttribute("orderProcessed");
        } catch (SQLException e) {
            NotificationUtil.sendNotification(
                request,
                "Errore nel recupero dei dati dell'ordine.",
                "error"
            );
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        request
            .getRequestDispatcher("/WEB-INF/views/common/checkout/confirm.jsp")
            .forward(request, response);
    }

    // --- HELPER METHODS ---

    private OrderAddressDTO createOrderAddressSnapshot(
        AddressDAO addressDAO,
        OrderAddressDAO orderAddressDAO,
        Integer addressId,
        String addressType
    ) throws SQLException {
        AddressDTO address = addressDAO.getById(addressId);
        if (address == null) {
            throw new SQLException(
                "Indirizzo di " + addressType + " non trovato"
            );
        }

        OrderAddressDTO orderAddress = new OrderAddressDTO();
        orderAddress.setStreetAddress(address.getStreetAddress());
        orderAddress.setCity(address.getCity());
        orderAddress.setState(address.getState());
        orderAddress.setPostalCode(address.getPostalCode());
        orderAddress.setCountry(address.getCountry());
        orderAddressDAO.save(orderAddress);

        return orderAddress;
    }

    private BigDecimal calculateOrderTotal(List<CartItemDetailDTO> cartItems) {
        return cartItems
            .stream()
            .map(item ->
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            )
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDTO createOrder(
        Integer userId,
        OrderAddressDTO shippingAddress,
        OrderAddressDTO billingAddress,
        BigDecimal total
    ) {
        OrderDTO order = new OrderDTO();
        order.setUserID(userId);
        order.setShippingAddressId(shippingAddress.getOrderAddressID());
        order.setBillingAddressId(billingAddress.getOrderAddressID());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(total);
        order.setOrderStatus(OrderDTO.OrderStatus.Processing);
        return order;
    }

    private void processOrderItem(
        ProductDAO productDAO,
        OrderItemDAO orderItemDAO,
        OrderDTO order,
        CartItemDetailDTO cartItem
    ) throws SQLException {
        // Re-fetch product to ensure we have the latest stock info
        ProductDTO product = productDAO.getById(cartItem.getProductId());

        // Double-check stock availability (race condition protection)
        if (product.getStockQuantity() < cartItem.getQuantity()) {
            throw new SQLException(
                "Scorte insufficienti per il prodotto: " +
                cartItem.getProductName() +
                " (Disponibili: " +
                product.getStockQuantity() +
                ", Richieste: " +
                cartItem.getQuantity() +
                ")"
            );
        }

        // Create order item
        OrderItemDTO orderItem = new OrderItemDTO();
        orderItem.setOrderID(order.getOrderID());
        orderItem.setProductID(cartItem.getProductId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setUnitPrice(cartItem.getPrice());

        // Set product snapshot - must be valid JSON!
        String productSnapshot = createProductSnapshot(product, cartItem);
        orderItem.setProductSnapshot(productSnapshot);

        orderItemDAO.save(orderItem);

        // Update stock quantity
        product.setStockQuantity(
            product.getStockQuantity() - cartItem.getQuantity()
        );
        productDAO.save(product);
    }

    // Helper method to create a valid JSON product snapshot
    private String createProductSnapshot(
        ProductDTO product,
        CartItemDetailDTO cartItem
    ) {
        // Create a properly escaped JSON representation
        StringBuilder json = new StringBuilder();
        json.append("{");
        json
            .append("\"productId\":")
            .append(product.getProductId())
            .append(",");
        json
            .append("\"name\":\"")
            .append(escapeJsonString(product.getProductName()))
            .append("\",");
        json.append("\"price\":").append(cartItem.getPrice()).append(",");
        json.append("\"imageId\":").append(cartItem.getImageId());
        json.append("}");
        return json.toString();
    }

    // Helper method to escape special characters in JSON strings
    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    private void clearUserCart(
        CartDAO cartDAO,
        CartItemDAO cartItemDAO,
        Integer userId
    ) throws SQLException {
        CartDTO userCart = cartDAO.getByUserId(userId);
        if (userCart != null) {
            // Get all cart items for this user's cart
            Collection<CartItemDTO> cartItems = cartItemDAO.getByCartId(
                userCart.getCartId(),
                null
            );

            // Delete each cart item individually using the CartItemDAO delete method
            for (CartItemDTO item : cartItems) {
                cartItemDAO.delete(item.getCartItemId());
            }
        }
    }

    private void cleanupCheckoutSession(HttpSession session) {
        session.removeAttribute("shippingAddressId");
        session.removeAttribute("billingAddressId");
    }

    private void handleCheckoutError(
        Connection conn,
        SQLException e,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        // Rollback transaction on error
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                // Log rollback error if needed
            }
        }

        String errorMessage = "Errore durante il checkout: " + e.getMessage();
        NotificationUtil.sendNotification(request, errorMessage, "error");
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                // Connection closing error - can be ignored in most cases
            }
        }
    }
}
