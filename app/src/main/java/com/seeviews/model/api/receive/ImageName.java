package com.seeviews.model.api.receive;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan-Willem on 2-12-2016.
 */
public class ImageName implements Parcelable {

    @SerializedName("name")
    String name;

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    public ImageName() {
    }

    protected ImageName(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<ImageName> CREATOR = new Parcelable.Creator<ImageName>() {
        @Override
        public ImageName createFromParcel(Parcel source) {
            return new ImageName(source);
        }

        @Override
        public ImageName[] newArray(int size) {
            return new ImageName[size];
        }
    };
}
