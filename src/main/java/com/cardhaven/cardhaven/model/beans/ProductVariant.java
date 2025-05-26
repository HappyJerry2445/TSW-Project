package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.util.Objects;

public class ProductVariant implements Serializable {
    private int variantId;
    private int productId;
    private String variantName;
    private String attributes;
    private double additionalPrice;
    private int stock;

    public ProductVariant() {

    }

    public ProductVariant(int variantId, int productId, String variantName, String attributes, double additionalPrice, int stock) {
        this.variantId = variantId;
        this.productId = productId;
        this.variantName = variantName;
        this.attributes = attributes;
        this.additionalPrice = additionalPrice;
        this.stock = stock;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public double getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(double additionalPrice) {
        this.additionalPrice = additionalPrice;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "ProductVariant{" +
                "variantId=" + variantId +
                ", productId=" + productId +
                ", variantName='" + variantName + '\'' +
                ", attributes='" + attributes + '\'' +
                ", additionalPrice=" + additionalPrice +
                ", stock=" + stock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductVariant that = (ProductVariant) o;
        return variantId == that.variantId && productId == that.productId && Double.compare(additionalPrice, that.additionalPrice) == 0 && stock == that.stock && Objects.equals(variantName, that.variantName) && Objects.equals(attributes, that.attributes);
    }
}
