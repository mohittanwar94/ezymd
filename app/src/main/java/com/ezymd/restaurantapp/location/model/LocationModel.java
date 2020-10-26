package com.ezymd.restaurantapp.location.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModel implements Parcelable {
    public String location="";
    public Double lang=0.0;
    public Double lat=0.0;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.location);
        dest.writeValue(this.lang);
        dest.writeValue(this.lat);
    }

    public LocationModel() {
    }

    protected LocationModel(Parcel in) {
        this.location = in.readString();
        this.lang = (Double) in.readValue(Double.class.getClassLoader());
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel source) {
            return new LocationModel(source);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };
}
