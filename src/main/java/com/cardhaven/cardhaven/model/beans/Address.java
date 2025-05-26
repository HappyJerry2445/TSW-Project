package com.cardhaven.cardhaven.model.beans; // Adjust package as needed

import java.io.Serializable;
import java.util.Objects;

public class Address implements Serializable {
    private int addressID;
    private int userID; // Foreign key to User table
    private String streetAddress;
    private String city;
    private String state; // Can be null
    private String postalCode;
    private String country;
    private AddressType addressType;
    private boolean isDefault;

    // Constructors
    public Address() {
    }

    public Address(int addressID, int userID, String streetAddress, String city, String state, String postalCode, String country, AddressType addressType, boolean isDefault) {
        this.addressID = addressID;
        this.userID = userID;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.addressType = addressType;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public int getAddressID() {
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return addressID == address.addressID &&
                userID == address.userID &&
                isDefault == address.isDefault &&
                Objects.equals(streetAddress, address.streetAddress) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(country, address.country) &&
                addressType == address.addressType;
    }

    @Override
    public String toString() {
        return "Address{" +
                "addressID=" + addressID +
                ", userID=" + userID +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                ", addressType=" + addressType +
                ", isDefault=" + isDefault +
                '}';
    }


    // Enum for address types
    public enum AddressType {
        Shipping,
        Billing
    }
}