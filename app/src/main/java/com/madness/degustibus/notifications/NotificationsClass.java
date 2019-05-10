package com.madness.degustibus.notifications;

public class NotificationsClass {

    private String date;
    private String description;
    private String type;

    public NotificationsClass() {
    }

    public NotificationsClass(String date, String description, String type) {
        this.date = date;
        this.description = description;
        this.type = type;
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
