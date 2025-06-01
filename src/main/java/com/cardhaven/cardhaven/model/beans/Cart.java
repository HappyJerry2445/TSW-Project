package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Cart implements Serializable {
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private int cartId;
    private Integer userId; // Null for guest carts
    private String sessionId; // Null for registered user carts

    public Cart() {
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", cartId=" + cartId +
                ", userId=" + userId +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return cartId == cart.cartId && Objects.equals(createdAt, cart.createdAt) && Objects.equals(lastUpdated, cart.lastUpdated) && Objects.equals(userId, cart.userId) && Objects.equals(sessionId, cart.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, lastUpdated, cartId, userId, sessionId);
    }
}
