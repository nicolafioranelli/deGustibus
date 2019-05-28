package com.madness.restaurant.daily;

public class DishClass {

    private String dish;
    private String description;
    private String avail;
    private Float price;
    private String pic;
    private String restaurant;
    private String identifier;

    public DishClass() {
    }

    public DishClass(String dish, String description, String avail, Float price, String pic, String restaurant, String identifier) {
        this.dish = dish;
        this.description = description;
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

    public String getDescription() { return description; }

    public void setDescription(String type) {
        this.description = type;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
