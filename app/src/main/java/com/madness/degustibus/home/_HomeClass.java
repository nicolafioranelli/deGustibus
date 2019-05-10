package com.madness.degustibus.home;

import android.os.Parcel;
import android.os.Parcelable;

public class _HomeClass implements Parcelable {

    public static final Creator<_HomeClass> CREATOR = new Creator<_HomeClass>() {
        @Override
        public _HomeClass createFromParcel(Parcel in) {
            return new _HomeClass(in);
        }

        @Override
        public _HomeClass[] newArray(int size) {
            return new _HomeClass[size];
        }
    };
    String name;
    String address;
    String desc;
    String pic;
    String id;
    private int identifier;

    protected _HomeClass(Parcel in) {
        name = in.readString();
        address = in.readString();
        desc = in.readString();
        pic = in.readString();
        id = in.readString();
    }

    public _HomeClass(){
        //required empty
    }
    public _HomeClass(String name, String address, String desc, String pic, String id) {
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
