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
    String name;
    String address;
    String desc;
    String pic;
    String id;
    private int identifier;

    protected HomeClass(Parcel in) {
        name = in.readString();
        address = in.readString();
        desc = in.readString();
        pic = in.readString();
        id = in.readString();
    }

    public HomeClass(){
        //required empty
    }
    public HomeClass(String name, String address, String desc, String pic,String id) {
        this.name = name;
        this.address = address;
        this.desc = desc;
        this.pic = pic;
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(desc);
        dest.writeString(pic);
        dest.writeString(id);
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

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String subtitle) {
        this.address = subtitle;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String description) {
        this.desc = description;
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
