package com.madness.restaurant.reservations;

public class RiderComparable {

    private Boolean available;
    private Double distance;
    private String name;
    private String photo;
    private String key;

    public RiderComparable() {
    }

    public RiderComparable(Boolean available, Double distance, String name, String photo, String key) {
        this.available = available;
        this.distance = distance;
        this.name = name;
        this.photo = photo;
        this.key = key;
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
}
