package com.madness.degustibus.order;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuClass implements Parcelable {

    public static final Creator<MenuClass> CREATOR = new Creator<MenuClass>() {
        @Override
        public MenuClass createFromParcel(Parcel in) {
            return new MenuClass(in);
        }

        @Override
        public MenuClass[] newArray(int size) {
            return new MenuClass[size];
        }
    };
    String dish;
    String type;
    String avail;
    String price;
    String pic;
    String restaurant;

    public MenuClass() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String idenifier) {
        this.identifier = idenifier;
    }

    String identifier;

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }


    protected MenuClass(Parcel in) {
        pic = in.readString();
        dish = in.readString();
        type = in.readString();
        avail = in.readString();
        price = in.readString();
    }

    public MenuClass(String dish, String type, String avail, String price, String pic) {
        this.dish = dish;
        this.type = type;
        this.avail = avail;
        this.price = price;
        this.pic = pic;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pic);
        dest.writeString(dish);
        dest.writeString(type);
        dest.writeString(avail);
        dest.writeString(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDish() {
        return dish;
    }

    public void setDish(String dish) {
        this.dish = dish;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAvail() {
        return avail;
    }

    public void setAvail(String avail) {
        this.avail = avail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
