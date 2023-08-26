package com.example.superdrop2.methods;


import com.example.superdrop2.adapter.CartItem;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String shippingName;
    private String shippingAddress;
    private String shippingCity;
    private String contactInstructions;
    private String note;
    private String paymentMethod,grandtotal;
    private List<CartItem> items;
    private String orderid;

    public Order() {
        items = new ArrayList<>();
        // Default constructor required for Firebase
    }

    public Order(String shippingName, String shippingAddress, String shippingCity,
                 String contactInstructions, String note, String paymentMethod) {
        this.shippingName = shippingName;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.contactInstructions = contactInstructions;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.items = new ArrayList<>(); // Initialize the items list
        // No need to add a dummy item her
    }
    private String status;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getOrderId() {
        return orderid;
    }

    public void setOrderId(String orderId) {
        this.orderid = orderId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getContactInstructions() {
        return contactInstructions;
    }

    public void setContactInstructions(String contactInstructions) {
        this.contactInstructions = contactInstructions;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGrandTotal() {
        return grandtotal;
    }

    public void setGrandTotal(String grandtotal) {
        this.grandtotal = grandtotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // Add getter and setter methods as needed
}
