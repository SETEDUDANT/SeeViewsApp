package com.seeviews.model.api.receive;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan-Willem on 1-12-2016.
 */
public class Preset implements Parcelable {

    @SerializedName("id")
    int id;
    @SerializedName("title")
    String title;
    @SerializedName("value")
    float value;

    public int getId() {
        return id;
    }

    public String getTitle() {
        if (title == null)
            title = "";
        return title;
    }

    public float getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeFloat(this.value);
    }

    public Preset() {
    }

    protected Preset(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.value = in.readFloat();
    }

    public static final Parcelable.Creator<Preset> CREATOR = new Parcelable.Creator<Preset>() {
        @Override
        public Preset createFromParcel(Parcel source) {
            return new Preset(source);
        }

        @Override
        public Preset[] newArray(int size) {
            return new Preset[size];
        }
    };
}
