package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class CartItemDTO implements Serializable {
    private int cartItemId;
    private int cartId;
    private int productId;
    private Integer variantId;
    private int quantity;
    private LocalDateTime addedAt;

    public CartItemDTO() {
    }

    public CartItemDTO(Integer cartItemId, Integer cartId, Integer productId, Integer variantId, int quantity, LocalDateTime addedAt) {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Integer getVariantId() {
        return variantId;
    }

    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDTO cartItemDTO = (CartItemDTO) o;
        return cartItemId == cartItemDTO.cartItemId && cartId == cartItemDTO.cartId && productId == cartItemDTO.productId && quantity == cartItemDTO.quantity && Objects.equals(variantId, cartItemDTO.variantId) && Objects.equals(addedAt, cartItemDTO.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartItemId, cartId, productId, variantId, quantity, addedAt);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", variantId=" + variantId +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                '}';
    }
}
