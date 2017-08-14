package com.seeviews.model.api.receive;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.seeviews.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 1-12-2016.
 */

public class Question implements Parcelable {

    @SerializedName("id")
    int id;
    @SerializedName("type")
    String type;
    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("contact_email")
    String contact_email;
    @SerializedName("review")
    Review review;
    @SerializedName("presets")
    ArrayList<Preset> presets;
    @SerializedName("images")
    ArrayList<ImageName> images;

    public String getType() {
        if (type == null)
            type = "";
        return type;
    }

    public String getTitle() {
        if (title == null)
            title = "";
        return title;
    }

    public String getDescription() {
        if (description == null)
            description = "";
        return description;
    }

    public String getContactEmail() {
        if (contact_email == null)
            contact_email = "";
        return contact_email;
    }

    public Review getReview() {
        return review;
    }

    public ArrayList<Preset> getPresets() {
        if (presets == null)
            presets = new ArrayList<>();
        return presets;
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
        dest.writeString(this.type);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeParcelable(this.review, flags);
        dest.writeList(this.presets);
        dest.writeList(this.images);
    }

    public Question() {
    }

    protected Question(Parcel in) {
        this.id = in.readInt();
        this.type = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.review = in.readParcelable(Review.class.getClassLoader());
        this.presets = new ArrayList<>();
        in.readList(this.presets, Preset.class.getClassLoader());
        this.images = new ArrayList<>();
        in.readList(this.images, ImageName.class.getClassLoader());
    }

    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel source) {
            return new Question(source);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getPresetIds() {
        ArrayList<Integer> res = new ArrayList<>();
        for (Preset p : getPresets())
            res.add(p.getId());
        return res;
    }

    public boolean hasSavedUserInput() {
        boolean hasPreset = false;

        for (Preset p : getPresets())
            if (p.getValue() > 0) {
                hasPreset = true;
                break;
            }

        return (getReview() != null && getReview().hasSavedUserInput())
                || hasPreset;
    }

    public boolean isComplete() {
        boolean presetsAreComplete = true;

        for (Preset p : getPresets()) {
            if (p.getValue() == 0) {
                presetsAreComplete = false;
                break;
            }
        }

        //Because Dylan sometimes sends me a question with no presets,
        // but then he still does want something here to be sent to the server...
        // Even though Comment and Media are not required.
        // Get your shit together dude.
        if (getPresets().size() == 0) {
            //Comment and media is not required, but enough when there are no presets
            return getReview() != null && getReview().hasMedia();
        } else
            return presetsAreComplete;
    }

    @Override
    public String toString() {
        String res = "\n +========================"
                + "\n | Question " + getId();

        if (getReview() != null) {
            Review r = getReview();
            if (!StringUtils.hasEmpty(r.getImage()))
                res += "\n | Image:   " + r.getImage();

            if (!StringUtils.hasEmpty(r.getVideo()))
                res += "\n | Video:   " + r.getVideo();

            if (!StringUtils.hasEmpty(r.getComment()))
                res += "\n | Comment: " + r.getComment();
        }
        if (getPresets().size() > 0) {
            res += "\n | Sliders: ";

            for (Preset p : getPresets()) {
                res += "\n | " + p.getId() + " -> " + p.getValue();
            }
        }

        res += "\n |========================";

        return res;
    }
}
