package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class ProductAttributeDTO implements Serializable {
	private int productID;
	private int attributeID;
	private String value;

	public ProductAttributeDTO() {
	}

	public ProductAttributeDTO(int productID, int attributeID, String value) {
		this.productID = productID;
		this.attributeID = attributeID;
		this.value = value;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public int getAttributeID() {
		return attributeID;
	}

	public void setAttributeID(int attributeID) {
		this.attributeID = attributeID;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductAttributeDTO that = (ProductAttributeDTO) o;
		return productID == that.productID &&
				attributeID == that.attributeID &&
				Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productID, attributeID, value);
	}

	@Override
	public String toString() {
		return "ProductAttributeDTO{" +
				"productID=" + productID +
				", attributeID=" + attributeID +
				", value='" + value + '\'' +
				'}';
	}
}