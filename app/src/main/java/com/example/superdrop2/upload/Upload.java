package com.example.superdrop2.upload;

public class Upload {
    private String itemId; // New field for unique item ID
    private String mName;
    private String mImageUrl;
    private Double mPrice;

    public Upload() {
        //empty constructor needed
    }
    public Upload(String name, String imageUrl, double price) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mPrice = price; // Set the price
    }
    // Add the getters and setters for the price variable
    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public Upload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
    }
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
}
