package com.svvaap.superdrop_admin.adapter;

public class Upload {
    private String mName;
    private String mImageUrl;
    private Double mPrice;
    private String mRestName;
    private String mItemId;
    private String mDiscount;
    private String mDiscountPrice;

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
    public Upload(String name, String imageUrl, double price,String restname,String itemId) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mPrice = price; // Set the price
        mRestName=restname;
        mItemId=itemId;
    }
    public Upload(String name, String imageUrl, Double price, String restName, String itemId, String discount, String discountPrice) {
        mName = name.trim().isEmpty() ? "No Name" : name;
        mImageUrl = imageUrl;
        mPrice = price;
        mRestName = restName;
        mItemId = itemId;
        mDiscount = discount;
        mDiscountPrice = discountPrice;
    }
    public Upload(String imageUrl,String itemId){
        mImageUrl = imageUrl;
        mItemId=itemId;
    }

    // Add the getters and setters for the price variable

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

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double price) {
        mPrice = price;
    }

    public String getRestName() {
        return mRestName;
    }

    public void setRestName(String restName) {
        mRestName = restName;
    }

    public String getItemId() {
        return mItemId;
    }

    public void setItemId(String itemId) {
        mItemId = itemId;
    }

    public String getDiscount() {
        return mDiscount;
    }

    public void setDiscount(String discount) {
        mDiscount = discount;
    }

    public String getDiscountPrice() {
        return mDiscountPrice;
    }

    public void setDiscountPrice(String discountPrice) {
        mDiscountPrice = discountPrice;
    }
}
