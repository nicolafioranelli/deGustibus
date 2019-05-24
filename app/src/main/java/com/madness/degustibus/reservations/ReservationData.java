package com.madness.degustibus.reservations;

public class ReservationData {

    private String customerID;
    private String restaurantID;
    private String deliverymanID;
    private String deliveryDate;
    private String deliveryHour;
    private String description;
    private String totalPrice;
    private String customerAddress;
    private String restaurantAddress;
    private String status;

    public ReservationData() {
    }

    public ReservationData(String customerID, String restaurantID, String deliverymanID, String deliveryDate, String deliveryHour, String description, String totalPrice, String customerAddress, String restaurantAddress, String status) {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        this.deliverymanID = deliverymanID;
        this.deliveryDate = deliveryDate;
        this.deliveryHour = deliveryHour;
        this.description = description;
        this.totalPrice = totalPrice;
        this.customerAddress = customerAddress;
        this.restaurantAddress = restaurantAddress;
        this.status = status;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getDeliverymanID() {
        return deliverymanID;
    }

    public void setDeliverymanID(String deliverymanID) {
        this.deliverymanID = deliverymanID;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}