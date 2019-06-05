package com.madness.degustibus.home;

public class RatingsClass {

    private String name;
    private Float value;
    private String comment;
    private String date;

    public RatingsClass() {
    }

    public RatingsClass(String name, Float value, String comment, String date) {
        this.name = name;
        this.value = value;
        this.comment = comment;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
