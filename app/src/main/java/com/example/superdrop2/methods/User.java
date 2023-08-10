package com.example.superdrop2.methods;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String fullName;
    private String phone;
    private String streetAddress;
    private String city;
    private String emergencyContact;
    private float rating;
    private String profileImageUrl; // New attribute to store image URL

    public User() {
        // Default constructor required for Firebase
    }

    public User(String fullName,String phone,String streetAddress,String city,String emergencyContact,float rating,String imageurl){
        this.fullName=fullName;
        this.phone=phone;
        this.streetAddress=streetAddress;
        this.city=city;
        this.emergencyContact=emergencyContact;
        this.rating=rating;
        this.profileImageUrl=imageurl;

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


}
