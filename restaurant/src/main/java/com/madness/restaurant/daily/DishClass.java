package com.madness.restaurant.daily;

public class DishClass {

    private String dish;
    private String description;
    private int avail;
    private Float price;
    private String pic;
    private String restaurant;
    private String identifier;
    private Long count;
    private Float rating;

    public DishClass(String dish, String description, int avail, Float price, String pic, String restaurant, String identifier, Long count, Float rating) {
        this.dish = dish;
        this.description = description;
        this.avail = avail;
        this.price = price;
        this.pic = pic;
        this.restaurant = restaurant;
        this.identifier = identifier;
        this.count = count;
        this.rating = rating;
    }

    public DishClass() {
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvail() {
        return avail;
    }

    public void setAvail(int avail) {
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

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
