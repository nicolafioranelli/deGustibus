package com.madness.restaurant.daily;

public class DailyClass {

    private String dish;
    private String type;
    private String avail;
    private String price;
    private String pic;
    private String restaurant;
    private String identifier;

    public DailyClass() {
    }

    public DailyClass(String dish, String type, String avail, String price, String pic, String restaurant, String identifier) {
        this.dish = dish;
        this.type = type;
        this.avail = avail;
        this.price = price;
        this.pic = pic;
        this.restaurant = restaurant;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String idenifier) {
        this.identifier = idenifier;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
