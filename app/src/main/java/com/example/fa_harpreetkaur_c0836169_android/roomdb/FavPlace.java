package com.example.fa_harpreetkaur_c0836169_android.roomdb;


import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class FavPlace implements  Serializable {

    @PrimaryKey(autoGenerate = true)
    int id;
    @ColumnInfo(name = "name")
    String name = "";

    public FavPlace() {
    }

    public FavPlace(int id, String name, String date, int isVisited, String lat, String lng) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.isVisited = isVisited;
        this.latitude = lat;
        this.longitude = lng;
    }

    public FavPlace(String name, String date, int isVisited, String lat, String lng) {

        this.name = name;
        this.date = date;
        this.isVisited = isVisited;
        this.latitude = lat;
        this.longitude = lng;
    }

    @ColumnInfo(name = "date")
    String date = "";


    @ColumnInfo(name = "latitude")
    String latitude = "0.0";

    @ColumnInfo(name = "longitude")
    String longitude = "0.0";

    @ColumnInfo(name = "isVisited")
    int isVisited = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getIsVisited() {
        return isVisited;
    }

    public void setIsVisited(int isVisited) {
        this.isVisited = isVisited;
    }


//    protected FavPlace(Parcel in) {
//        this(in.readInt(),
//                in.readString(),
//                in.readString(),
//                in.readInt(), in.readString(), in.readString());
//
//
//    }

//    public static final Creator<FavPlace> CREATOR = new Creator<FavPlace>() {
//        @Override
//        public FavPlace createFromParcel(Parcel in) {
//            return new FavPlace(in);
//        }
//
//        @Override
//        public FavPlace[] newArray(int size) {
//            return new FavPlace[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//    }
}
