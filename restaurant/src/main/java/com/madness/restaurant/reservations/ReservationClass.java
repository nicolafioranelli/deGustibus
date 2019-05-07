package com.madness.restaurant.reservations;

public class ReservationClass {

    private String fullname;
    private String identifier;
    private String dish;
    private String portions;
    private String datetime;

    public ReservationClass() {
    }

    public ReservationClass(String fullname, String identifier, String dish, String portions, String datetime) {
        this.fullname = fullname;
        this.identifier = identifier;
        this.dish = dish;
        this.portions = portions;
        this.datetime = datetime;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getPortions() {
        return portions;
    }

    public void setPortions(String portions) {
        this.portions = portions;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
