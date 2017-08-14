package com.seeviews.model.api.receive;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.seeviews.R;
import com.seeviews.utils.RatingUtils;
import com.seeviews.utils.StringUtils;

/**
 * Created by Jan-Willem on 29-11-2016.
 */
public class Review implements Parcelable {

    @SerializedName("id")
    int id;
    @SerializedName("comment")
    String comment;
    @SerializedName("image")
    String image;
    @SerializedName("video")
    String video;
    @SerializedName("time_ago")
    String time_ago;
    @SerializedName("user")
    String user;
    @SerializedName("sentiment")
    float sentiment;

    public String getComment() {
        if (comment == null)
            comment = "";
        return comment;
    }

    public String getImage() {
        if (image == null)
            image = "";
        return image;
    }

    public String getVideo() {
        if (video == null)
            video = "";
        return video;
    }

    public String getTime_ago() {
        if (time_ago == null)
            time_ago = "";
        return time_ago;
    }

    public String getUser() {
        if (user == null)
            user = "";
        return user;
    }

    public float getSentiment() {
        return sentiment;
    }

    @DrawableRes
    public int getRatingEmoticon() {
        float rating = getSentiment();
        if (rating < 5)
            return R.drawable.ic_emo_sad;
        else if (rating < 7)
            return R.drawable.ic_emo_neutral;
        else
            return R.drawable.ic_emo_excited;
    }

    @ColorRes
    public int getRatingColor() {
        return RatingUtils.getRatingColor(getSentiment());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.comment);
        dest.writeString(this.image);
        dest.writeString(this.video);
        dest.writeString(this.time_ago);
        dest.writeString(this.user);
        dest.writeFloat(this.sentiment);
    }

    public Review() {
    }

    protected Review(Parcel in) {
        this.id = in.readInt();
        this.comment = in.readString();
        this.image = in.readString();
        this.video = in.readString();
        this.time_ago = in.readString();
        this.user = in.readString();
        this.sentiment = in.readFloat();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public int getId() {
        return id;
    }

    public boolean hasSavedUserInput() {
        return StringUtils.isNotEmpty(comment)
                || StringUtils.isNotEmpty(image)
                || StringUtils.isNotEmpty(video);
    }

    public boolean hasMedia() {
        return StringUtils.isNotEmpty(getImage()) || StringUtils.isNotEmpty(getVideo());
    }

    public void clear() {
        this.image = "";
        this.video = "";
        this.comment = "";
    }
}
