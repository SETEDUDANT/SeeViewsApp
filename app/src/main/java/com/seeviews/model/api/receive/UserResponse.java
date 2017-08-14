package com.seeviews.model.api.receive;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan-Willem on 29-11-2016.
 */

public class UserResponse implements Parcelable {

    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("hotel")
    Hotel hotel;

    public int getId() {
        return id;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public Hotel getHotel() {
        if (hotel == null)
            hotel = new Hotel();
        return hotel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.hotel, flags);
    }

    public UserResponse() {
    }

    protected UserResponse(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.hotel = in.readParcelable(Hotel.class.getClassLoader());
    }

    public static final Parcelable.Creator<UserResponse> CREATOR = new Parcelable.Creator<UserResponse>() {
        @Override
        public UserResponse createFromParcel(Parcel source) {
            return new UserResponse(source);
        }

        @Override
        public UserResponse[] newArray(int size) {
            return new UserResponse[size];
        }
    };
}
