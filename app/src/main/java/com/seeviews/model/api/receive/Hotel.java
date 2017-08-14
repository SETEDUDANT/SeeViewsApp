package com.seeviews.model.api.receive;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 29-11-2016.
 */
public class Hotel implements Parcelable {

    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("reviews")
    ArrayList<Review> reviews;
    @SerializedName("images")
    ArrayList<ImageName> images;

    public Hotel(UserResponse u) {
        Hotel h = u == null ? new Hotel() : u.getHotel();
        this.id = h.getId();
        this.name = h.getName();
        this.reviews = h.getReviews();
        this.images = h.getImages();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public ArrayList<Review> getReviews() {
        if (reviews == null)
            reviews = new ArrayList<>();
        return reviews;
    }

    public ArrayList<ImageName> getImages() {
        if (images == null)
            images = new ArrayList<>();
        return images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeList(this.reviews);
        dest.writeList(this.images);
    }

    public Hotel() {
    }

    public Hotel(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.reviews = new ArrayList<>();
        in.readList(this.reviews, Review.class.getClassLoader());
        this.images = new ArrayList<>();
        in.readList(this.images, ImageName.class.getClassLoader());
    }

    public static final Parcelable.Creator<Hotel> CREATOR = new Parcelable.Creator<Hotel>() {
        @Override
        public Hotel createFromParcel(Parcel source) {
            return new Hotel(source);
        }

        @Override
        public Hotel[] newArray(int size) {
            return new Hotel[size];
        }
    };

    public boolean isValid() {
        return true; //TODO actually check. For now its not sure what to check.
    }
}
