package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A detailed view model for a cart item.
 * This class combines information from CartItemDTO, ProductDTO
 * to provide all necessary details for displaying a single item in the cart view.
 */
public class CartItemDetailDTO implements Serializable {
    private int cartItemId;
    private int cartId;
    private int quantity;
    private int productId;
    private int imageId;
    private String productName;
    private BigDecimal price;

    public CartItemDetailDTO() {
    }

    @Override
    public String toString() {
        return "CartItemDetailDTO{" +
                "cartItemId=" + cartItemId +
                ", cartId=" + cartId +
                ", quantity=" + quantity +
                ", productId=" + productId +
                ", imageId=" + imageId +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                '}';
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    // Getters and Setters for all fields

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
