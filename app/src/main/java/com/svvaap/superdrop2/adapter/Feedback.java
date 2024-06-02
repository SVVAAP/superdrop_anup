package com.svvaap.superdrop2.adapter;

public class Feedback {

    public String id;
    public String name;
    public String email;
    public String phone;
    public String message;

    public Feedback() {
        // Default constructor required for calls to DataSnapshot.getValue(Feedback.class)
    }

    public Feedback(String id, String name, String email, String phone, String message) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
    }
}

