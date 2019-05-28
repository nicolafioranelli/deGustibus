package com.madness.degustibus.new_reservations;

import java.util.HashMap;
import java.util.List;

public class OrderClass {

    private String customerID;
    private String restaurantID;
    private String deliverymanID;
    private String deliveryDate;
    private String deliveryHour;
    private String totalPrice;
    private String customerAddress;
    private String restaurantAddress;
    private String status;
    private String riderComment;
    private String riderRating;
    private String restaurantComment;
    private String restaurantRating;
    private List<ItemClass> cart;

    public OrderClass() {
    }

    public OrderClass(String customerID, String restaurantID, String deliverymanID, String deliveryDate, String deliveryHour, String totalPrice, String customerAddress, String restaurantAddress, String status, String riderComment, String riderRating, String restaurantComment, String restaurantRating, List<ItemClass> cart) {
        this.customerID = customerID;
        this.restaurantID = restaurantID;
        this.deliverymanID = deliverymanID;
        this.deliveryDate = deliveryDate;
        this.deliveryHour = deliveryHour;
        this.totalPrice = totalPrice;
        this.customerAddress = customerAddress;
        this.restaurantAddress = restaurantAddress;
        this.status = status;
        this.riderComment = riderComment;
        this.riderRating = riderRating;
        this.restaurantComment = restaurantComment;
        this.restaurantRating = restaurantRating;
        this.cart = cart;
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

    public String getRiderComment() {
        return riderComment;
    }

    public void setRiderComment(String riderComment) {
        this.riderComment = riderComment;
    }

    public String getRiderRating() {
        return riderRating;
    }

    public void setRiderRating(String riderRating) {
        this.riderRating = riderRating;
    }

    public String getRestaurantComment() {
        return restaurantComment;
    }

    public void setRestaurantComment(String restaurantComment) {
        this.restaurantComment = restaurantComment;
    }

    public String getRestaurantRating() {
        return restaurantRating;
    }

    public void setRestaurantRating(String restaurantRating) {
        this.restaurantRating = restaurantRating;
    }

    public List<ItemClass> getCart() {
        return cart;
    }

    public void setCart(List<ItemClass> cart) {
        this.cart = cart;
    }
}
