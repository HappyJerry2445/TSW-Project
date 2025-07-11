package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class OrderItemDTO implements Serializable {
    private int orderItemID;
    private int orderID;
    private Integer productID;
    private Integer variantID;
    private String productSnapshot;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItemDTO() {
    }

    public OrderItemDTO(int orderItemID, int orderID, Integer productID, Integer variantID, int quantity, BigDecimal unitPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.variantID = variantID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getProductSnapshot() {
        return productSnapshot;
    }

    public void setProductSnapshot(String productSnapshot) {
        this.productSnapshot = productSnapshot;
    }

    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public Integer getVariantID() {
        return variantID;
    }

    public void setVariantID(Integer variantID) {
        this.variantID = variantID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDTO that = (OrderItemDTO) o;
        return orderItemID == that.orderItemID &&
                orderID == that.orderID &&
                quantity == that.quantity &&
                Objects.equals(productID, that.productID) &&
                Objects.equals(variantID, that.variantID) &&
                Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderItemID, orderID, productID, variantID, quantity, unitPrice);
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "orderItemID=" + orderItemID +
                ", orderID=" + orderID +
                ", productID=" + productID +
                ", variantID=" + variantID +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }


}