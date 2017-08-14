package com.seeviews.model.internal;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.seeviews.model.api.receive.Preset;
import com.seeviews.model.api.receive.Question;
import com.seeviews.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public class Answer implements Parcelable {

    @SerializedName("questionId")
    int questionId;
    @SerializedName("imageLoc")
    String imageLoc;
    @SerializedName("videoLoc")
    String videoLoc;
    @SerializedName("comment")
    String comment;
    @SerializedName("sliderValues")
    HashMap<Integer, Float> sliderValues;

    public Answer(int questionId) {
        this.questionId = questionId;
    }

    public Answer(Answer answer) {
        this.questionId = answer.getQuestionId();
        this.imageLoc = answer.getImageLoc();
        this.videoLoc = answer.getVideoLoc();
        this.comment = answer.getComment();
        this.sliderValues = new HashMap<>();
        for (Map.Entry<Integer, Float> e : answer.getSliderValues().entrySet()) {
            sliderValues.put(e.getKey(), e.getValue());
        }
    }

    public Answer(Question question) {
        if (question != null) {
            questionId = question.getId();
            //Video, Image and Comment are set in local variables.
            sliderValues = new HashMap<>();
            for (Preset p : question.getPresets())
                sliderValues.put(p.getId(), p.getValue());
        }
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getImageLoc() {
        if (imageLoc == null)
            imageLoc = "";
        return imageLoc;
    }

    public void setImageLoc(String imageLoc) {
        this.imageLoc = imageLoc;
    }

    public String getVideoLoc() {
        if (videoLoc == null)
            videoLoc = "";
        return videoLoc;
    }

    public void setVideoLoc(String videoLoc) {
        this.videoLoc = videoLoc;
    }

    public String getComment() {
        if (comment == null)
            comment = "";
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public HashMap<Integer, Float> getSliderValues() {
        if (sliderValues == null)
            sliderValues = new HashMap<>();
        return sliderValues;
    }

    public void setSliderValues(HashMap<Integer, Float> sliderValues) {
        this.sliderValues = sliderValues;
    }

    public float getSliderValue(int sliderId) {
        Float res = getSliderValues().get(sliderId);
        return res == null ? 0 : res;
    }

    public void setSliderValue(int key, float value) {
        getSliderValues().put(key, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.questionId);
        dest.writeString(this.imageLoc);
        dest.writeString(this.videoLoc);
        dest.writeString(this.comment);
        dest.writeMap(sliderValues);
        Bundle b = new Bundle();
        for (Map.Entry<Integer, Float> e : sliderValues.entrySet())
            b.putFloat("" + e.getKey(), e.getValue());
        dest.writeBundle(b);
    }

    public Answer() {
    }

    protected Answer(Parcel in) {
        this.questionId = in.readInt();
        this.imageLoc = in.readString();
        this.videoLoc = in.readString();
        this.comment = in.readString();
        this.sliderValues = new HashMap<>();
        Bundle b = in.readBundle();
        for (String key : b.keySet())
            try {
                sliderValues.put(Integer.parseInt(key), b.getFloat(key));
            } catch (Exception ignored) {
            }
    }

    public static final Parcelable.Creator<Answer> CREATOR = new Parcelable.Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel source) {
            return new Answer(source);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    public boolean hasSomeInput() {
        boolean hasSliderValue = false;
        for (Map.Entry<Integer, Float> e : getSliderValues().entrySet()) {
            if (e.getValue() > 0)
                hasSliderValue = true;
        }
        return !StringUtils.hasEmpty(imageLoc)
                || !StringUtils.hasEmpty(videoLoc)
                || !StringUtils.hasEmpty(comment)
                || hasSliderValue;
    }

    public boolean hasBeenChangedFrom(Answer changedAnswer) {
        boolean slidersAreDifferent = false;

        HashMap<Integer, Float> changedSliders = changedAnswer.getSliderValues();
        if (getSliderValues().size() != changedSliders.size())
            slidersAreDifferent = true;
        else {
            for (Map.Entry<Integer, Float> e : getSliderValues().entrySet()) {
                if (changedSliders.containsKey(e.getKey())) {
                    if (getSliderValues().get(e.getKey()) != changedSliders.get(e.getKey())) {
                        slidersAreDifferent = true;
                        break;
                    }
                } else {
                    slidersAreDifferent = true;
                    break;
                }
            }
        }

        return !getImageLoc().equals(changedAnswer.getImageLoc())
                || !getVideoLoc().equals(changedAnswer.getVideoLoc())
                || !getComment().equals(changedAnswer.getComment())
                || slidersAreDifferent;
    }

    public boolean hasBeenChangedFrom(Question question) {
        if (question == null)
            return true;
        else {
            boolean differentPresets = false;

            for (Preset p : question.getPresets()) {
                if (p.getValue() != getSliderValue(p.getId())) {
                    differentPresets = true;
                    break;
                }
            }

            return differentPresets
                    || StringUtils.isNotEmpty(getComment())
                    || StringUtils.isNotEmpty(getImageLoc())
                    || StringUtils.isNotEmpty(getVideoLoc());
        }
    }

    @Override
    public String toString() {
        String res = "\n +========================"
                + "\n | Answer   " + getQuestionId();

        if (!StringUtils.hasEmpty(getImageLoc()))
            res += "\n | Image:   " + getImageLoc();

        if (!StringUtils.hasEmpty(getVideoLoc()))
            res += "\n | Video:   " + getVideoLoc();

        if (!StringUtils.hasEmpty(getComment()))
            res += "\n | Comment: " + getComment();

        if (getSliderValues().size() > 0) {
            res += "\n | Sliders: ";

            for (Map.Entry<Integer, Float> e : getSliderValues().entrySet()) {
                res += "\n | " + e.getKey() + " -> " + e.getValue();
            }
        }

        res += "\n |========================";

        return res;
    }

    public boolean isCompleted(@NonNull ArrayList<Integer> sliderIds) {
        boolean allSlidersAnswered = true;

        for (Integer i : sliderIds) {
            if (i != null && getSliderValue(i) == 0) {
                allSlidersAnswered = false;
            }
        }

        return allSlidersAnswered &&
                (!StringUtils.hasEmpty(getImageLoc())
                        || !StringUtils.hasEmpty(getVideoLoc()));
    }

    public boolean hasNoMedia() {
        return StringUtils.isEmpty(getImageLoc()) && StringUtils.isEmpty(getVideoLoc());
    }

    public void clearMedia() {
        imageLoc = "";
        videoLoc = "";
        comment = "";
    }
}
