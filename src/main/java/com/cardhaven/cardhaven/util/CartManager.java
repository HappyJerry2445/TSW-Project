package com.cardhaven.cardhaven.util;

import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductVariantDAO;
import com.cardhaven.cardhaven.model.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Manages all cart operations, including retrieving detailed cart items for display.
 */
public final class CartManager {

    private static final String GUEST_CART_SESSION_KEY = "guestCart";

    private CartManager() {
    }

    /**
     * Retrieves a fully detailed list of cart items for display purposes.
     * This method is the primary entry point for servlets needing to display the cart.
     * It fetches products and variants to build a list of CartItemDetailDTOs.
     *
     * @param request     The current HttpServletRequest.
     * @param cartDAO     The DAO for cart data.
     * @param cartItemDAO The DAO for cart item data.
     * @param productDAO  The DAO for product data.
     * @param variantDAO  The DAO for product variant data.
     * @return A list of detailed cart items ready for the view.
     * @throws SQLException If a database error occurs.
     */
    public static List<CartItemDetailDTO> getDetailedCartItems(HttpServletRequest request, CartDAO cartDAO, CartItemDAO cartItemDAO, ProductDAO productDAO, ProductVariantDAO variantDAO) throws SQLException {
        Collection<CartItemDTO> rawItems = getCartItems(request, cartDAO, cartItemDAO);
        List<CartItemDetailDTO> detailedItems = new ArrayList<>();

        for (CartItemDTO item : rawItems) {
            ProductDTO product = productDAO.getById(item.getProductId());
            ProductVariantDTO variant = (item.getVariantId() != null) ? variantDAO.getById(item.getVariantId()) : null;

            if (product != null) {
                CartItemDetailDTO detailDTO = new CartItemDetailDTO();
                detailDTO.setCartItemId(item.getCartItemId());
                detailDTO.setCartId(item.getCartId());
                detailDTO.setQuantity(item.getQuantity());
                detailDTO.setProductId(item.getProductId());
                detailDTO.setVariantId(item.getVariantId());
                detailDTO.setProductName(product.getProductName());
                detailDTO.setPrice(new BigDecimal(product.getBasePrice()));
                if (variant != null) {
                    detailDTO.setPrice(new BigDecimal(product.getBasePrice() + variant.getAdditionalPrice()));
                }
                detailedItems.add(detailDTO);
            }
        }
        return detailedItems;
    }

    /**
     * Updates the quantity of an item in the cart. Handles both guest and user carts.
     *
     * @param request     The HttpServletRequest to access the session.
     * @param cartDAO     The DAO for cart data, used for ownership verification.
     * @param cartItemDAO The DAO for cart item data.
     * @param cartItemId  The ID of the cart item to update.
     * @param quantity    The new quantity.
     * @throws SQLException If a database error occurs.
     */
    public static void updateItemQuantity(HttpServletRequest request, CartDAO cartDAO, CartItemDAO cartItemDAO, int cartItemId, int quantity) throws SQLException {
        if (quantity < 1) {
            deleteItem(request, cartDAO, cartItemDAO, cartItemId);
            return;
        }

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            CartItemDTO item = cartItemDAO.getById(cartItemId);
            if (item != null) {
                CartDTO cart = cartDAO.getById(item.getCartId());
                if (cart != null && Objects.equals(cart.getUserId(), userId)) {
                    item.setQuantity(quantity);
                    cartItemDAO.save(item);
                }
            }
        } else {
            Collection<CartItemDTO> guestCart = getGuestCart(session);
            guestCart.stream()
                    .filter(item -> item.getCartItemId() == cartItemId)
                    .findFirst()
                    .ifPresent(item -> item.setQuantity(quantity));
        }
    }

    /**
     * Deletes an item from the cart. Handles both guest and user carts.
     *
     * @param request     The HttpServletRequest to access the session.
     * @param cartDAO     The DAO for cart data, used for ownership verification.
     * @param cartItemDAO The DAO for cart item data.
     * @param cartItemId  The ID of the cart item to delete.
     * @throws SQLException If a database error occurs.
     */
    public static void deleteItem(HttpServletRequest request, CartDAO cartDAO, CartItemDAO cartItemDAO, int cartItemId) throws SQLException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            CartItemDTO item = cartItemDAO.getById(cartItemId);
            if (item != null) {
                CartDTO cart = cartDAO.getById(item.getCartId());
                if (cart != null && Objects.equals(cart.getUserId(), userId)) {
                    cartItemDAO.delete(cartItemId);
                }
            }
        } else {
            Collection<CartItemDTO> guestCart = getGuestCart(session);
            guestCart.removeIf(item -> item.getCartItemId() == cartItemId);
        }
    }

    private static Collection<CartItemDTO> getCartItems(HttpServletRequest request, CartDAO cartDAO, CartItemDAO cartItemDAO) throws SQLException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            mergeGuestCartWithUserCart(session, userId, cartDAO, cartItemDAO);
            CartDTO userCart = getOrCreateUserCart(userId, cartDAO);
            return cartItemDAO.getByCartId(userCart.getCartId(), null);
        } else {
            return getGuestCart(session);
        }
    }

    // ... private helper methods remain the same
    private static void mergeGuestCartWithUserCart(HttpSession session, int userId, CartDAO cartDAO, CartItemDAO cartItemDAO) throws SQLException {
        Collection<CartItemDTO> guestCart = (Collection<CartItemDTO>) session.getAttribute(GUEST_CART_SESSION_KEY);

        if (guestCart != null && !guestCart.isEmpty()) {
            CartDTO userCart = getOrCreateUserCart(userId, cartDAO);
            Collection<CartItemDTO> userCartItems = cartItemDAO.getByCartId(userCart.getCartId(), null);

            for (CartItemDTO guestItem : guestCart) {
                CartItemDTO existingItem = userCartItems.stream()
                        .filter(item -> Objects.equals(item.getVariantId(), guestItem.getVariantId()))
                        .findFirst()
                        .orElse(null);

                if (existingItem != null) {
                    existingItem.setQuantity(existingItem.getQuantity() + guestItem.getQuantity());
                    cartItemDAO.save(existingItem);
                } else {
                    guestItem.setCartId(userCart.getCartId());
                    guestItem.setCartItemId(0);
                    cartItemDAO.save(guestItem);
                }
            }
            session.removeAttribute(GUEST_CART_SESSION_KEY);
        }
    }

    private static Collection<CartItemDTO> getGuestCart(HttpSession session) {
        @SuppressWarnings("unchecked")
        Collection<CartItemDTO> guestCart = (Collection<CartItemDTO>) session.getAttribute(GUEST_CART_SESSION_KEY);
        if (guestCart == null) {
            guestCart = new ArrayList<>();
            session.setAttribute(GUEST_CART_SESSION_KEY, guestCart);
        }
        return guestCart;
    }

    private static CartDTO getOrCreateUserCart(int userId, CartDAO cartDAO) throws SQLException {
        CartDTO cart = cartDAO.getByUserId(userId);
        if (cart == null) {
            cart = new CartDTO();
            cart.setUserId(userId);
            cartDAO.save(cart);
        }
        return cart;
    }
}