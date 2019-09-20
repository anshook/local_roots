package com.udacity.ak.localroots.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity(tableName = "favorite_market")
public class Market implements Parcelable {

    @PrimaryKey
    @NonNull
    String id;
    String marketname;
    String miles;
    String address;
    String latitute;
    String longitude;
    String schedule;
    String products;

    public Market() {
    }

    //Constructor - read variables from Parcel
    @Ignore
    public Market(Parcel in) {
        id = in.readString();
        marketname = in.readString();
        miles = in.readString();
        address = in.readString();
        latitute = in.readString();
        longitude = in.readString();
        schedule = in.readString();
        products = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarketname() {
        return marketname;
    }

    public void setMarketname(String marketname) {
        this.marketname = marketname;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitute;
    }

    public void setLatitude(String latitude) {
        this.latitute = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(marketname);
        parcel.writeString(miles);
        parcel.writeString(address);
        parcel.writeString(latitute);
        parcel.writeString(longitude);
        parcel.writeString(schedule);
        parcel.writeString(products);
    }

    public static final Parcelable.Creator<Market> CREATOR = new Parcelable.Creator<Market>(){
        public Market createFromParcel(Parcel in) {
            return new Market(in);
        }
        public Market[] newArray(int size) {
            return new Market[size];
        }
    };
}

