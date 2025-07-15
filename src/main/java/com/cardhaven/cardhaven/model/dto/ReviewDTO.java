package com.cardhaven.cardhaven.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReviewDTO implements Serializable {
    private int reviewId;
    private int productId;
    private int userId;
    private int rating;
    private String title;
    private String reviewText;
    private LocalDateTime createdAt;
    private ReviewStatus reviewStatus;
    public ReviewDTO() {
    }


    public ReviewDTO(int reviewId, int productId, int userId, int rating, String title, String reviewText, LocalDateTime createdAt, ReviewStatus reviewStatus) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.title = title;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.reviewStatus = reviewStatus;
    }

    @Override
    public String toString() {
        return "ReviewDTO{" +
                "reviewId=" + reviewId +
                ", productId=" + productId +
                ", userId=" + userId +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", reviewText='" + reviewText + '\'' +
                ", createdAt=" + createdAt +
                ", reviewStatus=" + reviewStatus +
                '}';
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;

        ReviewDTO reviewDTO = (ReviewDTO) object;
        return reviewId == reviewDTO.reviewId && productId == reviewDTO.productId && userId == reviewDTO.userId && rating == reviewDTO.rating && Objects.equals(title, reviewDTO.title) && Objects.equals(reviewText, reviewDTO.reviewText) && Objects.equals(createdAt, reviewDTO.createdAt) && reviewStatus == reviewDTO.reviewStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId, productId, userId, rating, title, reviewText, createdAt, reviewStatus);
    }

    public enum ReviewStatus {
        Pending,
        Approved,
        Rejected;

        public String toItalian() {
            return switch (this) {
                case Pending -> "In attesa";
                case Approved -> "Approvato";
                case Rejected -> "Rifiutato";
            };
        }
    }


}
