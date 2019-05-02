package com.madness.degustibus.home;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeClass implements Parcelable {

    public static final Creator<HomeClass> CREATOR = new Creator<HomeClass>() {
        @Override
        public HomeClass createFromParcel(Parcel in) {
            return new HomeClass(in);
        }

        @Override
        public HomeClass[] newArray(int size) {
            return new HomeClass[size];
        }
    };
    String title;
    String subtitle;
    String description;
    String pic;
    private int identifier;

    protected HomeClass(Parcel in) {
        title = in.readString();
        subtitle = in.readString();
        description = in.readString();
        pic = in.readString();
    }

    public HomeClass(String title, String subtitle, String description, String pic) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.pic = pic;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(description);
        dest.writeString(pic);
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
