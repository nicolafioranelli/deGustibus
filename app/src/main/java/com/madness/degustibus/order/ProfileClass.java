package com.madness.degustibus.order;

/**
 * Model for the profile
 */

public class ProfileClass {

    private String name;
    private String email;
    private String desc;
    private String phone;
    private String address;
    private String photo;

    public ProfileClass() {
    }

    public ProfileClass(String name, String email, String desc, String phone, String address, String photo) {
        this.name = name;
        this.email = email;
        this.desc = desc;
        this.phone = phone;
        this.address = address;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}