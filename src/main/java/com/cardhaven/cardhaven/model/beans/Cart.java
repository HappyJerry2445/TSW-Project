package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Cart implements Serializable {
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private int cartId;
    private int userId;

    public Cart() {
    }

    public Cart(LocalDateTime createdAt, LocalDateTime lastUpdated, int cartId, Integer userId) {
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.cartId = cartId;
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", cartId=" + cartId +
                ", userId=" + userId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return cartId == cart.cartId && Objects.equals(createdAt, cart.createdAt) && Objects.equals(lastUpdated, cart.lastUpdated) && Objects.equals(userId, cart.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, lastUpdated, cartId, userId);
    }
}
