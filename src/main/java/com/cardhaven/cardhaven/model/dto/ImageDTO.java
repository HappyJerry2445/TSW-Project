package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public class ImageDTO implements Serializable {
    private int imageId;
    private String mimeType;
    private byte[] imageData;

    public ImageDTO() {
    }

    public ImageDTO(int imageId, String mimeType, byte[] imageData) {
        this.imageId = imageId;
        this.mimeType = mimeType;
        this.imageData = imageData;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImageDTO imageDTO = (ImageDTO) o;
        return imageId == imageDTO.imageId && Objects.equals(mimeType, imageDTO.mimeType) && Objects.deepEquals(imageData, imageDTO.imageData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageId, mimeType, Arrays.hashCode(imageData));
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
                "imageId=" + imageId +
                ", mimeType='" + mimeType + '\'' +
                ", imageData=" + Arrays.toString(imageData) +
                '}';
    }
}