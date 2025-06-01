package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProductDTO implements Serializable {

    private int productId;
    private String sku;
    private String productName;
    private double basePrice;
    private double currentPrice;
    private int stockQuantity;
    private ProductType productType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
    private boolean isActive;

    public ProductDTO() {
    }


    public ProductDTO(int productId, String sku, String productName, double basePrice, double currentPrice, int stockQuantity, ProductType productType, LocalDateTime createdAt, LocalDateTime lastUpdated, boolean isActive) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.basePrice = basePrice;
        this.currentPrice = currentPrice;
        this.stockQuantity = stockQuantity;
        this.productType = productType;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
        this.isActive = isActive;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
                "productId=" + productId +
                ", sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                ", basePrice=" + basePrice +
                ", currentPrice=" + currentPrice +
                ", stockQuantity=" + stockQuantity +
                ", productType=" + productType +
                ", createdAt=" + createdAt +
                ", lastUpdated=" + lastUpdated +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductDTO productDTO = (ProductDTO) o;
        return productId == productDTO.productId && Double.compare(basePrice, productDTO.basePrice) == 0 && Double.compare(currentPrice, productDTO.currentPrice) == 0 && stockQuantity == productDTO.stockQuantity && isActive == productDTO.isActive && Objects.equals(sku, productDTO.sku) && Objects.equals(productName, productDTO.productName) && productType == productDTO.productType && Objects.equals(createdAt, productDTO.createdAt) && Objects.equals(lastUpdated, productDTO.lastUpdated);
    }

    public enum ProductType {
        TradingCard,
        Accessory,
        BoosterPack
    }
}
