// ProductCategory.java
package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class ProductCategoryDTO implements Serializable {
    private ProductCategoryKey key;

    public ProductCategoryDTO(int productId, int categoryId) {
        this.key = new ProductCategoryKey(productId, categoryId);
    }

    public ProductCategoryDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ProductCategoryDTO that = (ProductCategoryDTO) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "key=" + key +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }


    public int getProductId() {
        return key.getProductId();
    }

    public void setProductId(int productId) {
        key.setProductId(productId);
    }

    public int getCategoryId() {
        return key.getCategoryId();
    }

    public void setCategoryId(int categoryId) {
        key.setCategoryId(categoryId);
    }


    static public class ProductCategoryKey implements Serializable {
        private int productId;
        private int categoryId;

        public ProductCategoryKey(int productId, int categoryId) {
            this.productId = productId;
            this.categoryId = categoryId;
        }

        public ProductCategoryKey() {
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            ProductCategoryKey that = (ProductCategoryKey) o;
            return productId == that.productId && categoryId == that.categoryId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, categoryId);
        }

        @Override
        public String toString() {
            return "ProductCategoryKey{" +
                    "productId=" + productId +
                    ", categoryId=" + categoryId +
                    '}';
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }
    }
}