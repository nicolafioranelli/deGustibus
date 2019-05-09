package com.madness.deliveryman.incoming;

public class IncomingData {

    private String Idcustomer;
    private String CostumerName;
    private String address;
    private String CostumerPhone;
    private String date;
    private String description;
    private String hour;
    private String idRest;
    private String RestName;
    private String RestAddress;
    private String price;
    private String priceProd;
    private String priceShip;
    private String state;

    public String getCostumerName() {
        return CostumerName;
    }

    public void setCostumerName(String costumerName) {
        CostumerName = costumerName;
    }

    public String getCostumerPhone() {
        return CostumerPhone;
    }

    public void setCostumerPhone(String costumerPhone) {
        CostumerPhone = costumerPhone;
    }

    public String getRestName() {
        return RestName;
    }

    public void setRestName(String restName) {
        RestName = restName;
    }

    public String getRestAddress() {
        return RestAddress;
    }

    public void setRestAddress(String restAddress) {
        RestAddress = restAddress;
    }


    public IncomingData() {
    }

    public IncomingData(String idcustomer, String address, String date, String description, String hour, String idRest, String price, String priceProd, String priceShip, String state, String title) {
        Idcustomer = idcustomer;
        this.address = address;
        this.date = date;
        this.description = description;
        this.hour = hour;
        this.idRest = idRest;
        this.price = price;
        this.priceProd = priceProd;
        this.priceShip = priceShip;
        this.state = state;
        this.title = title;
    }

    public String getIdcustomer() {
        return Idcustomer;
    }

    public void setIdcustomer(String idcustomer) {
        Idcustomer = idcustomer;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getIdRest() {
        return idRest;
    }

    public void setIdRest(String idRest) {
        this.idRest = idRest;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceProd() {
        return priceProd;
    }

    public void setPriceProd(String priceProd) {
        this.priceProd = priceProd;
    }

    public String getPriceShip() {
        return priceShip;
    }

    public void setPriceShip(String priceShip) {
        this.priceShip = priceShip;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
}
