package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class OrderItemDTO implements Serializable {
	private int orderItemID;
	private int orderID;
	private Integer productID;
	private Integer variantID;
	private int quantity;
	private BigDecimal unitPrice;
	private LocalDateTime addedAt;

	public OrderItemDTO() {
	}

	public OrderItemDTO(int orderItemID, int orderID, Integer productID, Integer variantID, int quantity, BigDecimal unitPrice, LocalDateTime addedAt) {
		this.orderItemID = orderItemID;
		this.orderID = orderID;
		this.productID = productID;
		this.variantID = variantID;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.addedAt = addedAt;
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

	public LocalDateTime getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
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
				Objects.equals(unitPrice, that.unitPrice) &&
				Objects.equals(addedAt, that.addedAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(orderItemID, orderID, productID, variantID, quantity, unitPrice, addedAt);
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
				", addedAt=" + addedAt +
				'}';
	}
}