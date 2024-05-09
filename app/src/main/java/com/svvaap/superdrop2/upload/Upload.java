package com.svvaap.superdrop_admin.adapter;

public class Upload {
    private String mName;
    private String mImageUrl;
    private Double mPrice;
    private String mRestId;
    private String mItemId;
    private String mDiscount;
    private String mDiscountPrice;
    private String mCatogery;
    private String mFoodType;

    public Upload() {
        //empty constructor needed
    }
    public Upload(String name, String imageUrl, double price,String restId,String itemId) {
        if (name.trim().equals("")) {
            name = "No Name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mPrice = price; // Set the price
        mRestId=restId;
        mItemId=itemId;
    }
    public Upload(String name, String imageUrl, Double price, String restId, String itemId, String discount, String discountPrice) {
        mName = name.trim().isEmpty() ? "No Name" : name;
        mImageUrl = imageUrl;
        mPrice = price;
        mRestId = restId;
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

    public String getRestId() {
        return mRestId;
    }

    public void setRestId(String restName) {
        mRestId = restName;
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

    public String getmCatogery() {
        return mCatogery;
    }

    public void setmCatogery(String mCatogery) {
        this.mCatogery = mCatogery;
    }

    public String getmFoodType() {
        return mFoodType;
    }

    public void setmFoodType(String mFoodType) {
        this.mFoodType = mFoodType;
    }
}