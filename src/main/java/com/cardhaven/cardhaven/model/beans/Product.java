package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Product implements Serializable {

    public enum ProductType {
        TradingCard,
        Accessory,
        BoosterPack
    }

    private int id;
    private String sku;
    private String productName;
    private double basePrice;
    private double currentPrice;
    private int stockQuantity;
    private int categoryId;
    private ProductType productType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private boolean isActive;


    public Product() {
    }

    public Product(int id, String sku, String productName, double basePrice, double currentPrice, int stockQuantity, int categoryId, ProductType productType, LocalDateTime createdAt, LocalDateTime lastUpdated, boolean isActive) {
        this.id = id;
        this.sku = sku;
        this.productName = productName;
        this.basePrice = basePrice;
        this.currentPrice = currentPrice;
        this.stockQuantity = stockQuantity;
        this.categoryId = categoryId;
        this.productType = productType;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                ", basePrice=" + basePrice +
                ", currentPrice=" + currentPrice +
                ", stockQuantity=" + stockQuantity +
                ", categoryId=" + categoryId +
                ", productType=" + productType +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", isActive=" + isActive +
                '}';
    }
}
