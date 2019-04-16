package com.madness.restaurant.reservations;

public class ReservationClass {

    private String name;
    private int identifier;
    private String seats;
    private String date_time;
    private String orderDishes;
    private String desc;

    public String getOrderDishes() {
        return orderDishes;
    }

    public void setOrderDishes(String orderDishes) {
        this.orderDishes = orderDishes;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }


    public String getSeats() {
        return seats;
    }

    public void setSeats(String seat) {
        this.seats = seat;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}