package com.madness.degustibus.new_reservations;

public class ItemClass {

    private Integer quantity;
    private String id;
    private String dish;
    private Integer rating;

    public ItemClass() {
    }

    public ItemClass(Integer quantity, String id, String dish, Integer rating) {
        this.quantity = quantity;
        this.id = id;
        this.dish = dish;
        this.rating = rating;
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
