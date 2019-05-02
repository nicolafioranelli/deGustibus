package com.madness.degustibus.notifications;

import android.os.Parcel;
import android.os.Parcelable;

public class NotificationsClass implements Parcelable {

    public static final Creator<NotificationsClass> CREATOR = new Creator<NotificationsClass>() {
        @Override
        public NotificationsClass createFromParcel(Parcel in) {
            return new NotificationsClass(in);
        }

        @Override
        public NotificationsClass[] newArray(int size) {
            return new NotificationsClass[size];
        }
    };
    String title;
    String description;
    String date;
    String hour;
    private int identifier;

    protected NotificationsClass(Parcel in) {
        title = in.readString();
        date = in.readString();
        description = in.readString();
        hour = in.readString();
    }

    public NotificationsClass(String title, String description, String date, String hour) {
        this.title = title;
        this.date = date;
        this.description = description;
        this.hour = hour;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(hour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}

