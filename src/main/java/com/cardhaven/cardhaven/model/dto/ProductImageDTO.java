package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class ProductImageDTO implements Serializable {
    private int productImageId;
    private int productId;
    private int sortOrder;
    private int imageId;

    public ProductImageDTO() {
    }

    public ProductImageDTO(int productImageId, int productId, int sortOrder, int imageId) {
        this.productImageId = productImageId;
        this.productId = productId;
        this.sortOrder = sortOrder;
        this.imageId = imageId;
    }

    public int getProductImageId() {
        return productImageId;
    }

    public void setProductImageId(int productImageId) {
        this.productImageId = productImageId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductImageDTO that = (ProductImageDTO) o;
        return productImageId == that.productImageId && productId == that.productId && sortOrder == that.sortOrder && imageId == that.imageId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productImageId, productId, sortOrder, imageId);
    }

    @Override
    public String toString() {
        return "ProductImageDTO{" +
                "productImageId=" + productImageId +
                ", productId=" + productId +
                ", sortOrder=" + sortOrder +
                ", imageId=" + imageId +
                '}';
    }
}
