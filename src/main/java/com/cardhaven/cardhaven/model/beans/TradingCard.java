package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

// TradingCard now extends the updated Product bean
public class TradingCard extends Product implements Serializable {

    private String cardSet;
    private String cardNumber;
    private Rarity rarity;
    private CardCondition cardCondition; // Can be null
    private String cardText; // Can be null
    private String artist; // Can be null
    private Integer yearPublished; // Use Integer to allow nulls for YEAR type

    // Constructors
    public TradingCard() {
        super(); // Call default Product constructor
    }

    // Constructor that calls super constructor for Product fields
    public TradingCard(int id, String sku, String productName, double basePrice, double currentPrice,
                       int stockQuantity, int categoryId, ProductType productType,
                       LocalDateTime createdAt, LocalDateTime lastUpdated, boolean isActive,
                       String cardSet, String cardNumber, Rarity rarity, CardCondition cardCondition,
                       String cardText, String artist, Integer yearPublished) {
        // Call Product constructor with matching parameters
        super(id, sku, productName, basePrice, currentPrice, stockQuantity, categoryId, productType,
                createdAt, lastUpdated, isActive);
        this.cardSet = cardSet;
        this.cardNumber = cardNumber;
        this.rarity = rarity;
        this.cardCondition = cardCondition;
        this.cardText = cardText;
        this.artist = artist;
        this.yearPublished = yearPublished;
    }

    // Getters and Setters (only for TradingCard specific fields)
    public String getCardSet() {
        return cardSet;
    }

    public void setCardSet(String cardSet) {
        this.cardSet = cardSet;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public CardCondition getCardCondition() {
        return cardCondition;
    }

    public void setCardCondition(CardCondition cardCondition) {
        this.cardCondition = cardCondition;
    }

    public String getCardText() {
        return cardText;
    }

    public void setCardText(String cardText) {
        this.cardText = cardText;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(Integer yearPublished) {
        this.yearPublished = yearPublished;
    }

    // Override equals and toString to include superclass fields and own fields
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Important: call super.equals()
        TradingCard that = (TradingCard) o;
        return Objects.equals(cardSet, that.cardSet) &&
                Objects.equals(cardNumber, that.cardNumber) &&
                rarity == that.rarity &&
                cardCondition == that.cardCondition &&
                Objects.equals(cardText, that.cardText) &&
                Objects.equals(artist, that.artist) &&
                Objects.equals(yearPublished, that.yearPublished);
    }

    @Override
    public String toString() {
        return "TradingCard{" +
                "cardSet='" + cardSet + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", rarity=" + rarity +
                ", cardCondition=" + cardCondition +
                ", cardText='" + cardText + '\'' +
                ", artist='" + artist + '\'' +
                ", yearPublished=" + yearPublished +
                "} " + super.toString();
    }

    // Enum for Rarity
    public enum Rarity {
        Common,
        Uncommon,
        Rare,
        Mythic,
        Secret
    }

    // Enum for CardCondition
    public enum CardCondition {
        Mint,
        Near_Mint,
        Lightly_Played,
        Moderately_Played,
        Heavily_Played
    }
}