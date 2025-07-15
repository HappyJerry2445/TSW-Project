// Order.java

package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderDTO implements Serializable {

    private int orderID;    //PK
    private int userID;        //FK of User
    private LocalDateTime orderDate;
    private OrderStatus orderStatus; //Enum
    private int shippingAddressId;        //FK to ShippingAddress
    private int billingAddressId;        //FK to BillingAddress
    private BigDecimal totalAmount;

    public OrderDTO() {

    }

    public OrderDTO(int orderID, int userID, LocalDateTime orderDate, OrderStatus orderStatus, int shippingAddressId, int billingAddressId, BigDecimal totalAmount) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.shippingAddressId = shippingAddressId;
        this.billingAddressId = billingAddressId;
        this.totalAmount = totalAmount;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        OrderDTO orderDTO = (OrderDTO) object;
        return orderID == orderDTO.orderID && userID == orderDTO.userID && shippingAddressId == orderDTO.shippingAddressId && billingAddressId == orderDTO.billingAddressId && totalAmount == orderDTO.totalAmount && Objects.equals(orderDate, orderDTO.orderDate) && Objects.equals(orderStatus, orderDTO.orderStatus);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderID + '\'' +
                ", userId=" + userID + '\'' +
                ", orderDate=" + orderDate + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", shippingAddressId=" + shippingAddressId + '\'' +
                ", billingAddressId=" + billingAddressId + '\'' +
                ", totalAmount=" + totalAmount + '\'' +
                '}';

    }

    @Override
    public int hashCode() {
        return Objects.hash(orderID, userID, orderDate, orderStatus, shippingAddressId, billingAddressId, totalAmount);
    }

    public enum OrderStatus {
        Pending,
        Processing,
        Shipped,
        Delivered,
        Cancelled;

        @Override
        public String toString() {
            return switch (this) {
                case Pending -> "In attesa";
                case Processing -> "In elaborazione";
                case Shipped -> "Spedito";
                case Delivered -> "Consegnato";
                case Cancelled -> "Annullato";
                default -> "";
            };
        }
    }
}
