package com.madness.restaurant.reservations;

public class OrderData {

    private String orderKey;
    private String customerAddress;
    private String customerID;
    private String deliveryDate;
    private String deliveryHour;
    private String riderID;
    private String riderName;
    private String restaurantID;
    private String restaurantName;
    private String totalPrice;
    private String description;
    private String status;

    public OrderData() {
    }

    public OrderData(String orderKey, String customerAddress, String customerID, String deliveryDate, String deliveryHour, String riderID, String riderName, String restaurantID, String restaurantName, String totalPrice, String description, String status) {
        this.orderKey = orderKey;
        this.customerAddress = customerAddress;
        this.customerID = customerID;
        this.deliveryDate = deliveryDate;
        this.deliveryHour = deliveryHour;
        this.riderID = riderID;
        this.riderName = riderName;
        this.restaurantID = restaurantID;
        this.restaurantName = restaurantName;
        this.totalPrice = totalPrice;
        this.description = description;
        this.status = status;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryHour() {
        return deliveryHour;
    }

    public void setDeliveryHour(String deliveryHour) {
        this.deliveryHour = deliveryHour;
    }

    public String getRiderID() {
        return riderID;
    }

    public void setRiderID(String riderID) {
        this.riderID = riderID;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
