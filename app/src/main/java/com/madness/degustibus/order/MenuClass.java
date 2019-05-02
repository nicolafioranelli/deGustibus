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
    String title;
    String description;
    String price;
    String quantity;
    String pic;
    private int identifier;

    protected MenuClass(Parcel in) {
        title = in.readString();
        description = in.readString();
        price = in.readString();
        quantity = in.readString();
        pic = in.readString();
    }

    public MenuClass(String title, String description, String price, String quantity, String pic) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.pic = pic;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(quantity);
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}

