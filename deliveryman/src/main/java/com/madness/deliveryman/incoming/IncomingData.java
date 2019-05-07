package com.madness.deliveryman.incoming;

public class IncomingData {

    private String restaurateur;
    private String customer;
    private String address;
    private String date;
    private String hour;

    public IncomingData() {
    }

    public IncomingData(String restaurateur, String customer, String address, String date, String hour) {
        this.restaurateur = restaurateur;
        this.customer = customer;
        this.address = address;
        this.date = date;
        this.hour = hour;
    }

    public String getRestaurateur() {
        return restaurateur;
    }

    public void setRestaurateur(String restaurateur) {
        this.restaurateur = restaurateur;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
