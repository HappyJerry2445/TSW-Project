// ProductCategory.java
package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.util.Objects;

public class ProductCategory implements Serializable {
    private int productId;
    private int categoryId;

    public ProductCategory() {
    }

    public ProductCategory(int productId, int categoryId) {
        this.productId = productId;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "productId=" + productId +
                ", categoryId=" + categoryId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductCategory that = (ProductCategory) o;
        return productId == that.productId && categoryId == that.categoryId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}