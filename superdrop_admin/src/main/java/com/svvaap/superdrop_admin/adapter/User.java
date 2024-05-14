package com.svvaap.superdrop_admin.adapter;

public class User {
    private String fullName;
    private String phone;
    private String registred;
    private String streetAddress;
    private String restName;
    private String restCity;
    private String token, restId;
    private String city;
    private String type;
    private String emergencyContact;
    private String restProfileImageUrl; // New attribute to store image URL

    public User() {
        // Default constructor required for Firebase
    }

    public User(String fullName, String phoneNumber, String phoneNumberoptl, String address, String restName, String restCity, String type, String restProfileImageUrl, String restId, String token, String registred) {
        this.fullName = fullName;
        this.phone = phoneNumber;
        this.emergencyContact = phoneNumberoptl;
        this.streetAddress = address;
        this.restName = restName;
        this.restCity = restCity;
        this.restProfileImageUrl = restProfileImageUrl;
        this.type = type;
        this.restId = restId;
        this.token = token;
        this.registred = registred;
    }


    public User(String restId, String token) {
        this.restId = restId;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public void setRegistred(String registred) {
        this.registred = registred;
    }

    public String getRegistred() {
        return registred;
    }

    public String getRestName() {
        return restName;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }

    public String getRestCity() {
        return restCity;
    }

    public void setRestCity(String restCity) {
        this.restCity = restCity;
    }

    public String getRestProfileImageUrl() {
        return restProfileImageUrl;
    }

    public void setRestProfileImageUrl(String restProfileImageUrl) {
        this.restProfileImageUrl = restProfileImageUrl;
    }

}
