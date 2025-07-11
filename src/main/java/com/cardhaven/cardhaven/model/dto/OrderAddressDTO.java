package com.cardhaven.cardhaven.model.dto;

import java.util.Objects;

public class OrderAddressDTO {
    private int orderAddressID;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    public OrderAddressDTO() {
    }

    public OrderAddressDTO(int orderAddressID, String streetAddress, String city, String state, String postalCode, String country) {
        this.orderAddressID = orderAddressID;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }

    public int getOrderAddressID() {
        return orderAddressID;
    }

    public void setOrderAddressID(int orderAddressID) {
        this.orderAddressID = orderAddressID;
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

    @Override
    public String toString() {
        return "OrderAddressDTO{" +
                "orderAddressID=" + orderAddressID +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderAddressDTO that = (OrderAddressDTO) o;
        return orderAddressID == that.orderAddressID && Objects.equals(streetAddress, that.streetAddress) && Objects.equals(city, that.city) && Objects.equals(state, that.state) && Objects.equals(postalCode, that.postalCode) && Objects.equals(country, that.country);
    }

}
