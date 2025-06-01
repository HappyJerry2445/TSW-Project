package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class CartDTO implements Serializable {
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private int cartId;
    private int userId;

    public CartDTO() {
    }

    public CartDTO(LocalDateTime createdAt, LocalDateTime lastUpdated, int cartId, Integer userId) {
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
        CartDTO cartDTO = (CartDTO) o;
        return cartId == cartDTO.cartId && Objects.equals(createdAt, cartDTO.createdAt) && Objects.equals(lastUpdated, cartDTO.lastUpdated) && Objects.equals(userId, cartDTO.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, lastUpdated, cartId, userId);
    }
}
