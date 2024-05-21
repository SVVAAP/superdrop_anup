package com.svvaap.superdrop_admin.adapter;



import java.util.ArrayList;
import java.util.List;

public class Order {
    private String shippingName,ctoken;
    private String shippingAddress;
    private String shippingCity,landmark,phone_optnl;
    private String contactInstructions;
    private String note;
    private String paymentMethod,grandtotal;
    private List<CartItem> items;
    private String orderid;
    private String userid;
    private String status,date,time;
    private String orderStatus;


    public Order() {
        items = new ArrayList<>();
        // Default constructor required for Firebase
    }

    public Order(String orderid,String shippingName, String shippingAddress, String shippingCity,
                 String contactInstructions, String phone_optnl, String note, String paymentMethod,String status,String grandtotal,String oredrStatus,String landmark,String token) {
        this.orderid=orderid;
        this.shippingName = shippingName;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.contactInstructions = contactInstructions;
        this.note = note;
        this.paymentMethod = paymentMethod;
        this.status=status;
        this.grandtotal=grandtotal;
        this.items = new ArrayList<>(); // Initialize the items list
        // No need to add a dummy item her
        this.orderStatus=oredrStatus;
        this.landmark=landmark;
        this.phone_optnl=phone_optnl;
        this.ctoken=token;
    }
    public String getOrderStatus(){
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus){
        this.orderStatus=orderStatus;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getUserId() {
        return userid;
    }
    public void setUserId(String userid) {
        this.userid = userid;
    }
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
    public String getLandmark() {
        return landmark;
    }
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    public String getPhone_optnl() {
        return phone_optnl;
    }
    public void setPhone_optnl(String phone_optnl) {
        this.phone_optnl = phone_optnl;
    }
    // Add getter and setter methods as needed
    public String getCtoken() {
        return ctoken;
    }

    public void setCtoken(String ctoken) {
        this.ctoken = ctoken;
    }
}
