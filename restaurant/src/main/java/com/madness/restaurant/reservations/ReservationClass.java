package com.madness.restaurant.reservations;

import android.os.Parcel;
import android.os.Parcelable;

public class ReservationClass implements Parcelable {

    private String name;
    private int identifier;
    private String seats;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String date;
        private String time;
    private String orderDishes;
    private String desc;

    public static final Creator<ReservationClass> CREATOR = new Creator<ReservationClass>() {
        @Override
        public ReservationClass createFromParcel(Parcel in) {
            return new ReservationClass(in);
        }

        @Override
        public ReservationClass[] newArray(int size) {
            return new ReservationClass[size];
        }
    };

    public String getOrderDishes() {
        return orderDishes;
    }

    public ReservationClass (Parcel parcel) {
        this.name = parcel.readString();
        this.identifier = parcel.readInt();
        this.seats = parcel.readString();
        this.date = parcel.readString();
        this.time = parcel.readString();
        this.orderDishes = parcel.readString();
        this.desc = parcel.readString();
    }
    public ReservationClass (String name,int identifier,String seats,String date, String time,String orderDishes,String desc ) {
        this.name = name;
        this.identifier = identifier;
        this.seats = seats;
        this.date = date;
        this.time = time;
        this.orderDishes = orderDishes;
        this.desc = desc;
    }
    public void setOrderDishes(String orderDishes) {
        this.orderDishes = orderDishes;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }


    public String getSeats() {
        return seats;
    }

    public void setSeats(String seat) {
        this.seats = seat;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(seats);
        dest.writeString(date);;
        dest.writeString(time);
        dest.writeString(desc);
        dest.writeInt(identifier);
        dest.writeString(orderDishes);
    }
}