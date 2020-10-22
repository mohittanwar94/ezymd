package com.ezymd.restaurantapp.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import retrofit2.Response;


public class BaseResponse implements Parcelable {

    public final static Creator<BaseResponse> CREATOR = new Creator<BaseResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BaseResponse createFromParcel(Parcel in) {
            return new BaseResponse(in);
        }

        public BaseResponse[] newArray(int size) {
            return (new BaseResponse[size]);
        }

    };
    @SerializedName("response")
    @Expose
    private Response response;

    protected BaseResponse(Parcel in) {
        this.response = ((Response) in.readValue((Response.class.getClassLoader())));
    }

    public BaseResponse() {
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(response);
    }

    public int describeContents() {
        return 0;
    }
}
