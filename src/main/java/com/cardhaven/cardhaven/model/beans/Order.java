// Order.java

package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Order implements Serializable{

	private int orderId;	//PK
	private int userId;		//FK of User
	private LocalDateTime orderDate;
	private String orderStatus; //Enum
	private int shippingAddressId;		//FK to ShippingAddress
	private int billingAddressId;		//FK to BillingAddress
	private int totalAmount;

	public Order() {

	}

	public Order(int orderId, int userId, LocalDateTime orderDate, String orderStatus, int shippingAddressId, int billingAddressId, int totalAmount) {
		this.orderId = orderId;
		this.userId = userId;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.shippingAddressId = shippingAddressId;
		this.billingAddressId = billingAddressId;
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
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
		if (object ==null || getClass() != object.getClass()) return false;

		Order order = (Order) object;
		return orderId == order.orderId && userId == order.userId && shippingAddressId == order.shippingAddressId && billingAddressId == order.billingAddressId && totalAmount == order.totalAmount && Objects.equals(orderDate, order.orderDate) && Objects.equals(orderStatus, order.orderStatus);
	}

	@Override
	public String toString() {
		return "Order{"+
				"orderId="+ orderId + '\''+
				", userId=" + userId + '\''+
				", orderDate=" + orderDate + '\''+
				", orderStatus='" + orderStatus + '\''+
				", shippingAddressId=" + shippingAddressId + '\''+
				", billingAddressId=" + billingAddressId + '\''+
				", totalAmount=" + totalAmount + '\''+
				'}';

	}

	@Override
	public int hashCode() {
		return Objects.hash(orderId, userId, orderDate, orderStatus, shippingAddressId, billingAddressId, totalAmount);
	}
}
