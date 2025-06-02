// Order.java

package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderDTO implements Serializable {

    private int orderId;    //PK
    private int userId;        //FK of User
    private LocalDateTime orderDate;
    private OrderStatus orderStatus; //Enum
    private int shippingAddressId;        //FK to ShippingAddress
    private int billingAddressId;        //FK to BillingAddress
    private int totalAmount;

    public OrderDTO() {

    }

    public OrderDTO(int orderId, int userId, LocalDateTime orderDate, OrderStatus orderStatus, int shippingAddressId, int billingAddressId, int totalAmount) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(int shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public int getBillingAddressId() {
        return billingAddressId;
    }

    public void setBillingAddressId(int billingAddressId) {
        this.billingAddressId = billingAddressId;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        OrderDTO orderDTO = (OrderDTO) object;
        return orderId == orderDTO.orderId && userId == orderDTO.userId && shippingAddressId == orderDTO.shippingAddressId && billingAddressId == orderDTO.billingAddressId && totalAmount == orderDTO.totalAmount && Objects.equals(orderDate, orderDTO.orderDate) && Objects.equals(orderStatus, orderDTO.orderStatus);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId + '\'' +
                ", userId=" + userId + '\'' +
                ", orderDate=" + orderDate + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", shippingAddressId=" + shippingAddressId + '\'' +
                ", billingAddressId=" + billingAddressId + '\'' +
                ", totalAmount=" + totalAmount + '\'' +
                '}';

    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, userId, orderDate, orderStatus, shippingAddressId, billingAddressId, totalAmount);
    }

    public enum OrderStatus{
        Pending,
        Processing,
        Shipped,
        Delivered,
        Cancelled
    }
}
