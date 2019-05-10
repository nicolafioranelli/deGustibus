package com.madness.degustibus.home;

public class RestaurantClass {
    private String address;
    private String desc;
    private String phone;
    private String email;
    private String name;
    private String photo;

    public RestaurantClass(String address, String desc, String phone, String email, String name, String photo) {
        this.address = address;
        this.desc = desc;
        this.phone = phone;
        this.email = email;
        this.name = name;
        this.photo = photo;
    }

    public RestaurantClass() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
