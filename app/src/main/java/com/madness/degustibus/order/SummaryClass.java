package com.madness.degustibus.order;

import android.os.Parcel;
import android.os.Parcelable;

public class SummaryClass implements Parcelable {

    public static final Creator<SummaryClass> CREATOR = new Creator<SummaryClass>() {
        @Override
        public SummaryClass createFromParcel(Parcel in) {
            return new SummaryClass(in);
        }

        @Override
        public SummaryClass[] newArray(int size) {
            return new SummaryClass[size];
        }
    };
    String dishname;
    String price;
    String quantity;
    String id ;
    private int identifier;

    protected SummaryClass(Parcel in) {
        dishname = in.readString();
        price = in.readString();
        quantity = in.readString();
        id = null;
    }

    public SummaryClass(String title, String price, String quantity) {
        this.dishname = title;
        this.price = price;
        this.quantity = quantity;
        id = null;
    }

    public SummaryClass(){
        // Required empty public constructor
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dishname);
        dest.writeString(price);
        dest.writeString(quantity);
        id = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

