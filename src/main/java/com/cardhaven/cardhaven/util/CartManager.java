package com.cardhaven.cardhaven.util;

import com.cardhaven.cardhaven.model.dao.CartDAO;
import com.cardhaven.cardhaven.model.dao.CartItemDAO;
import com.cardhaven.cardhaven.model.dao.ProductDAO;
import com.cardhaven.cardhaven.model.dao.ProductImageDAO;
import com.cardhaven.cardhaven.model.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.util.*;

public final class CartManager {

    private static final String GUEST_CART_SESSION_KEY = "guestCart";

    private CartManager() {
    }

    /**
     * Adds an item to the cart. Handles both guest and user carts.
     * If the item already exists, it increases the quantity.
     *
     * @param request     The HttpServletRequest to access the session.
     * @param cartDAO     The DAO for cart data.
     * @param cartItemDAO The DAO for cart item data.
     * @param productId   The ID of the product to add.
     * @param quantity    The quantity to add.
     * @throws SQLException If a database error occurs.
     */
    public static void addItem(HttpServletRequest request, CartDAO cartDAO, CartItemDAO cartItemDAO, ProductDAO productDAO, int productId, int quantity) throws SQLException, IllegalArgumentException {
        ProductDTO product = productDAO.getById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            CartDTO userCart = getOrCreateUserCart(userId, cartDAO);
            Collection<CartItemDTO> userCartItems = cartItemDAO.getByCartId(userCart.getCartId(), null);

            Optional<CartItemDTO> existingItemOpt = userCartItems.stream()
                    .filter(item -> item.getProductId() == productId)
                    .findFirst();

            if (existingItemOpt.isPresent()) {
                CartItemDTO existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                cartItemDAO.save(existingItem);
            } else {
                CartItemDTO newItem = new CartItemDTO();
                newItem.setCartId(userCart.getCartId());
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                cartItemDAO.save(newItem);
            }
        } else {
            Collection<CartItemDTO> guestCart = getGuestCart(session);
            Optional<CartItemDTO> existingItemOpt = guestCart.stream()
                    .filter(item -> item.getProductId() == productId)
                    .findFirst();

            if (existingItemOpt.isPresent()) {
                CartItemDTO existingItem = existingItemOpt.get();
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
            } else {
                CartItemDTO newItem = new CartItemDTO();
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                // For guests, we'll use the productId as the cartItemId for session management.
                // This allows update/delete operations to work on the guest cart.
                newItem.setCartItemId(productId);
                guestCart.add(newItem);
            }
        }
    }

    // Previous methods like getDetailedCartItems, updateItemQuantity, deleteItem, etc. would be here...

    public static List<CartItemDetailDTO> getDetailedCartItems(HttpServletRequest request, CartDAO cartDAO, CartItemDAO cartItemDAO, ProductDAO productDAO, ProductImageDAO productImageDAO) throws SQLException {
        Collection<CartItemDTO> rawItems = getCartItems(request, cartDAO, cartItemDAO);
        List<CartItemDetailDTO> detailedItems = new ArrayList<>();

        for (CartItemDTO item : rawItems) {
            ProductDTO product = productDAO.getById(item.getProductId());
            ProductImageDTO productImage = productImageDAO.getFirstByProductId(item.getProductId());

            if (product != null) {
                CartItemDetailDTO detailDTO = new CartItemDetailDTO();
                detailDTO.setCartItemId(item.getCartItemId());
                detailDTO.setCartId(item.getCartId());
                detailDTO.setQuantity(item.getQuantity());
                detailDTO.setProductId(item.getProductId());
                detailDTO.setProductName(product.getProductName());
                detailDTO.setPrice(product.getCurrentPrice());
                detailDTO.setImageId(productImage != null ? productImage.getImageId() : 0);
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
            // For guests, cartItemId is the productId
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
                Optional<CartItemDTO> existingItemOpt = userCartItems.stream()
                        .filter(item -> item.getProductId() == guestItem.getProductId())
                        .findFirst();

                if (existingItemOpt.isPresent()) {
                    CartItemDTO existingItem = existingItemOpt.get();
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