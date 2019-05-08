package com.madness.restaurant.notifications;

public class NotificationsClass {

    private String type;
    private String description;
    private String date;

    public NotificationsClass() {
    }

    public NotificationsClass(String type, String description, String date) {
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
