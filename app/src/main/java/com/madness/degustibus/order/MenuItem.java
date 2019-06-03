package com.madness.degustibus.order;

public class MenuItem {

    private Integer quantity;
    private String id;
    private String dish;
    private Integer rating;
    private Integer popular;
    private Integer avail;

    public MenuItem() {
    }

    public MenuItem(Integer quantity, String id, String dish, Integer rating, Integer popular, Integer avail) {
        this.quantity = quantity;
        this.id = id;
        this.dish = dish;
        this.rating = rating;
        this.popular = popular;
        this.avail = avail;
    }

    public Integer getAvail() {
        return avail;
    }

    public void setAvail(Integer avail) {
        this.avail = avail;
    }

    public Integer getPopular() {
        return popular;
    }

    public void setPopular(Integer popular) {
        this.popular = popular;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
