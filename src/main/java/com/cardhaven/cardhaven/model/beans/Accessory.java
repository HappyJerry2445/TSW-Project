// Accessory.java
package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.util.Objects;

public class Accessory implements Serializable {
    private int accessoryId; // Foreign key to Product
    private String accessoryType; // ENUM ('Sleeves', 'Binders', 'Dice', 'Playmats', 'Boxes')
    private String material;
    private String color;
    private String dimensions;
    private String compatibility;

    public Accessory() {
    }

    public Accessory(int accessoryId, String accessoryType, String material, String color, String dimensions, String compatibility) {
        this.accessoryId = accessoryId;
        this.accessoryType = accessoryType;
        this.material = material;
        this.color = color;
        this.dimensions = dimensions;
        this.compatibility = compatibility;
    }

    // Getters and Setters
    public int getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(int accessoryId) {
        this.accessoryId = accessoryId;
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(String accessoryType) {
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

    @Override
    public String toString() {
        return "Accessory{" +
                "accessoryId=" + accessoryId +
                ", accessoryType='" + accessoryType + '\'' +
                ", material='" + material + '\'' +
                ", color='" + color + '\'' +
                ", dimensions='" + dimensions + '\'' +
                ", compatibility='" + compatibility + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accessory accessory = (Accessory) o;
        return accessoryId == accessory.accessoryId &&
                Objects.equals(accessoryType, accessory.accessoryType) &&
                Objects.equals(material, accessory.material) &&
                Objects.equals(color, accessory.color) &&
                Objects.equals(dimensions, accessory.dimensions) &&
                Objects.equals(compatibility, accessory.compatibility);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessoryId, accessoryType, material, color, dimensions, compatibility);
    }
}