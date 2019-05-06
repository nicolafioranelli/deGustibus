package com.madness.degustibus.order;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CartClass implements Parcelable {

    public static final Creator<CartClass> CREATOR = new Creator<CartClass>() {
        @Override
        public CartClass createFromParcel(Parcel in) {
            return new CartClass(in);
        }

        @Override
        public CartClass[] newArray(int size) {
            return new CartClass[size];
        }
    };
    String dishname;
    String price;
    String quantity;
    private int identifier;

    protected CartClass(Parcel in) {
        dishname = in.readString();
        price = in.readString();
        quantity = in.readString();
    }

    public CartClass(String title, String price, String quantity) {
        this.dishname = title;
        this.price = price;
        this.quantity = quantity;
    }

    public CartClass(){

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dishname);
        dest.writeString(price);
        dest.writeString(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return dishname;
    }

    public void setTitle(String dishname) {
        this.dishname = dishname;
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

