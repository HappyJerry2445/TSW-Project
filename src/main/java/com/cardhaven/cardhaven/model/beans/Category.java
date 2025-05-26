package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.util.Objects;

public class Category implements Serializable {
    private int id;
    private String name;
    private Integer parentId;
    private String type;
    private String description;

    public Category() {
    }

    public Category(int id, String name, Integer parentId, String type, String description) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.type = type;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId=" + parentId +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;
        return id == category.id && Objects.equals(name, category.name) && Objects.equals(parentId, category.parentId) && Objects.equals(type, category.type) && Objects.equals(description, category.description);
    }

}
