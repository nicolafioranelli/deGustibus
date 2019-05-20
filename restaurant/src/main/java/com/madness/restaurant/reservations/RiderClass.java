package com.madness.restaurant.reservations;

public class RiderClass {

    private Boolean available;
    private Double latitude;
    private Double longitude;

    public RiderClass() {
    }

    public RiderClass(Boolean available, Double latitude, Double longitude) {
        this.available = available;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
