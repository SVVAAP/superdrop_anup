package com.example.superdrop2.methods;

import java.util.HashMap;
import java.util.Map;

public class User {


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fullName", fullName);
        result.put("phone", phone);
        result.put("streetAddress", streetAddress);
        result.put("city", city);
        result.put("emergencyContact", emergencyContact);
        result.put("rating", rating);
        // Include other attributes here, such as profileImageUrl

        return result;
    }
    private String fullName;
    private String phone;
    private String streetAddress;
    private String city;
    private String emergencyContact;
    private float rating;
    private String profileImageUrl; // New attribute to store image URL

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    // Constructors, getters, setters, etc.
}
