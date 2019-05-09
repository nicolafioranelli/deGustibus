package com.madness.degustibus.order;

import android.os.Parcel;

public class Dish {

    private String dish;
    private String desc;
    private int avail;
    private float price;
    private String pic;
    private String restaurant;
    private int quantity;
    private String identifier;

    public Dish() {
    }

    public Dish(String dish, String desc, int avail, float price, String pic, String restaurant, int quantity, String identifier) {
        this.dish = dish;
        this.desc = desc;
        this.avail = avail;
        this.price = price;
        this.pic = pic;
        this.restaurant = restaurant;
        this.quantity = quantity;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAvail() {
        return avail;
    }

    public void setAvail(int avail) {
        this.avail = avail;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
