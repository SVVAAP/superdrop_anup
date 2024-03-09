package com.svvaap.superdrop_admin.adapter;

public class CartItem {
    private String itemName;
    private double itemPrice,totalprice;
    private int quantity;

    private String itemimg;
    private String itemId;

    public CartItem() {
        // Default constructor required for Firebase
    }

    public CartItem(String itemName, double itemPrice, int quantity, double totalprice, String itemimg) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.itemimg=itemimg;
        this.totalprice=totalprice;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }
    public double getTotalprice() {
        return totalprice;
    }

    public int getQuantity() {
        return quantity;
    }
    public String getImageUrl(){
        return itemimg;
    }
    public String getItemId() {
        return itemId;
    }

    // Setter methods (if needed)
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setImageUrl(String itemimg){
        this.itemimg=itemimg;
    }
    public void setTotalprice(double totalprice) {
        this.totalprice = totalprice;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;}
}