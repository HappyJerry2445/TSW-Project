package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Accessory extends Product implements Serializable {
    private AccessoryType accessoryType;
    private String material; // Can be null
    private String color;    // Can be null
    private String dimensions; // Can be null
    private String compatibility; // Can be null

    // Constructors
    public Accessory() {
        super(); // Call default Product constructor
    }

    // Constructor that calls super constructor for Product fields
    public Accessory(int id, String sku, String productName, double basePrice, double currentPrice,
                     int stockQuantity, ProductType productType,
                     LocalDateTime createdAt, LocalDateTime lastUpdated, boolean isActive,
                     AccessoryType accessoryType, String material, String color,
                     String dimensions, String compatibility) {
        // Call Product constructor with matching parameters
        super(id, sku, productName, basePrice, currentPrice, stockQuantity, productType,
                createdAt, lastUpdated, isActive);
        this.accessoryType = accessoryType;
        this.material = material;
        this.color = color;
        this.dimensions = dimensions;
        this.compatibility = compatibility;
    }

    // Getters and Setters (only for Accessory specific fields)
    public AccessoryType getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(AccessoryType accessoryType) {
        this.accessoryType = accessoryType;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getCompatibility() {
        return compatibility;
    }

    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }


    // Override equals and toString to include superclass fields and own fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Accessory accessory = (Accessory) o;
        return accessoryType == accessory.accessoryType &&
                Objects.equals(material, accessory.material) &&
                Objects.equals(color, accessory.color) &&
                Objects.equals(dimensions, accessory.dimensions) &&
                Objects.equals(compatibility, accessory.compatibility);
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "accessoryType=" + accessoryType +
                ", material='" + material + '\'' +
                ", color='" + color + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", compatibility='" + compatibility + '\'' +
                "} " + super.toString();
    }

    // Enum for AccessoryType
    public enum AccessoryType {
        Sleeves,
        Binders,
        Dice,
        Playmats,
        Boxes
    }


}