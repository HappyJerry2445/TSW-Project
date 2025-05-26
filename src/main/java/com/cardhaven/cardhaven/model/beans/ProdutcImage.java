package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class ProdutcImage implements Serializable {
    private int imageId;
    private int productId;
    private byte[] imageData;
    private String mimeType;
    private String description;
    private int sortOrder;
    private LocalDateTime createdAt;
    private byte[] thumbnailData;
    private String thumbnailMimeType;

    public ProdutcImage() {

    }

    public ProdutcImage(int imageId, int productId, byte[] imageData, String mimeType, String description, int sortOrder, LocalDateTime createdAt, byte[] thumbnailData, String thumbnailMimeType) {
        this.imageId = imageId;
        this.productId = productId;
        this.imageData = imageData;
        this.mimeType = mimeType;
        this.description = description;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
        this.thumbnailData = thumbnailData;
        this.thumbnailMimeType = thumbnailMimeType;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public String getThumbnailMimeType() {
        return thumbnailMimeType;
    }

    public void setThumbnailMimeType(String thumbnailMimeType) {
        this.thumbnailMimeType = thumbnailMimeType;
    }

    @Override
    public String toString() {
        return "ProdutcImage{" +
                "imageId=" + imageId +
                ", productId=" + productId +
                ", imageData=" + Arrays.toString(imageData) +
                ", mimeType='" + mimeType + '\'' +
                ", description='" + description + '\'' +
                ", sortOrder=" + sortOrder +
                ", createdAt=" + createdAt +
                ", thumbnailData=" + Arrays.toString(thumbnailData) +
                ", thumbnailMimeType='" + thumbnailMimeType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProdutcImage that = (ProdutcImage) o;
        return imageId == that.imageId && productId == that.productId && sortOrder == that.sortOrder && Objects.deepEquals(imageData, that.imageData) && Objects.equals(mimeType, that.mimeType) && Objects.equals(description, that.description) && Objects.equals(createdAt, that.createdAt) && Objects.deepEquals(thumbnailData, that.thumbnailData) && Objects.equals(thumbnailMimeType, that.thumbnailMimeType);
    }
}
