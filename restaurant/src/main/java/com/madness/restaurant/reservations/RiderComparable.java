package com.madness.restaurant.reservations;

public class RiderComparable {

    private Boolean available;
    private Double distance;
    private String name;
    private String photo;
    private String key;
    private int count;
    private float rating;

    public RiderComparable() {
    }

    public RiderComparable(Boolean available, Double distance, String name, String photo, String key, int count, float rating) {
        this.available = available;
        this.distance = distance;
        this.name = name;
        this.photo = photo;
        this.key = key;
        this.count = count;
        this.rating = rating;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
