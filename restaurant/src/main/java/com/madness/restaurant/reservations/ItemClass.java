package com.madness.restaurant.reservations;

public class ItemClass {

    private Integer quantity;
    private String id;
    private String name;
    private Integer rating;

    public ItemClass() {
    }

    public ItemClass(Integer quantity, String id, String name, Integer rating) {
        this.quantity = quantity;
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
