package com.example.superdrop2.adapter;

import android.widget.ImageView;

public class CartItem {
    private String itemName;
    private double itemPrice;
    private int quantity;

    private String itemimg;

    public CartItem() {
        // Default constructor required for Firebase
    }

    public CartItem(String itemName, double itemPrice, int quantity,String itemimg) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.itemimg=itemimg;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }
    public String getItemimg(){
        return itemimg;
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
    public void setItemimg(String itemimg){
        this.itemimg=itemimg;
    }
}