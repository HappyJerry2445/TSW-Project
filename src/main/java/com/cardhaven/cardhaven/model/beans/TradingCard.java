// TradingCard.java
package com.cardhaven.cardhaven.model.beans;

import java.io.Serializable;
import java.util.Objects;

public class TradingCard implements Serializable {
    private int cardId; // Foreign key to Product
    private String cardSet;
    private String cardNumber;
    private String rarity; // ENUM ('Common', 'Uncommon', 'Rare', 'Mythic', 'Secret')
    private String cardCondition; // ENUM ('Mint', 'Near Mint', 'Lightly Played', 'Moderately Played', 'Heavily Played')
    private String cardText;
    private String artist;
    private Integer yearPublished; // YEAR type in SQL is usually int in Java

    public TradingCard() {
    }

    public TradingCard(int cardId, String cardSet, String cardNumber, String rarity, String cardCondition, String cardText, String artist, Integer yearPublished) {
        this.cardId = cardId;
        this.cardSet = cardSet;
        this.cardNumber = cardNumber;
        this.rarity = rarity;
        this.cardCondition = cardCondition;
        this.cardText = cardText;
        this.artist = artist;
        this.yearPublished = yearPublished;
    }

    // Getters and Setters
    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

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

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getCardCondition() {
        return cardCondition;
    }

    public void setCardCondition(String cardCondition) {
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

    @Override
    public String toString() {
        return "TradingCard{" +
                "cardId=" + cardId +
                ", cardSet='" + cardSet + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", rarity='" + rarity + '\'' +
                ", cardCondition='" + cardCondition + '\'' +
                ", cardText='" + cardText + '\'' +
                ", artist='" + artist + '\'' +
                ", yearPublished=" + yearPublished +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradingCard that = (TradingCard) o;
        return cardId == that.cardId &&
                Objects.equals(cardSet, that.cardSet) &&
                Objects.equals(cardNumber, that.cardNumber) &&
                Objects.equals(rarity, that.rarity) &&
                Objects.equals(cardCondition, that.cardCondition) &&
                Objects.equals(cardText, that.cardText) &&
                Objects.equals(artist, that.artist) &&
                Objects.equals(yearPublished, that.yearPublished);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId, cardSet, cardNumber, rarity, cardCondition, cardText, artist, yearPublished);
    }
}