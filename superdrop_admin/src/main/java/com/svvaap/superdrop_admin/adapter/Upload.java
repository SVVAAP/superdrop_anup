package com.svvaap.superdrop_admin.adapter;

public class Upload {
    private String mitemId; // New field for unique item ID
    private String mName;
    private String mImageUrl;
    private Double mPrice;
    private  String mrestname;
    private  String mdiscount;
    private  String mdiscountPrice;

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
        mrestname=restname;
        mitemId=itemId;
    }
    public Upload(String name, String imageUrl, double price,String restname,String itemId,String discount,String discountPrice) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mPrice = price; // Set the price
        mrestname=restname;
        mitemId=itemId;
        mdiscount=discount;
        mdiscountPrice=discountPrice;
    }
    public Upload(String imageUrl,String mitemId){
        this.mImageUrl = imageUrl;
        this.mitemId=mitemId;
    }

    // Add the getters and setters for the price variable
    public double getPrice() {
        return mPrice;
    }

    public void setPrice(double price) {
        mPrice = price;
    }
    public String getDiscount(){return mdiscount;}
    public void setDiscount(String discount){mdiscount=discount;}
    public String getDiscountPrice(){return mdiscountPrice;}
    public void setDiscountPrice(String discountPrice){mdiscountPrice=discountPrice;}

    public String getItemId() {
        return mitemId;
    }

    public void setRestName(String restName) {
        this.mrestname = restName;
    }

    public String getRestName() {
        return mrestname;
    }
    public void setItemId(String itemId) {
        this.mitemId = itemId;
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
