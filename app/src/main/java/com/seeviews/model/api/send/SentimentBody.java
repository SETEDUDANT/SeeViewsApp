package com.seeviews.model.api.send;

import com.google.gson.annotations.SerializedName;
import com.seeviews.model.api.receive.Preset;
import com.seeviews.model.api.receive.Review;

import java.util.ArrayList;

/**
 * Created by Jan-Willem on 14-12-2016.
 */

public class SentimentBody {

    @SerializedName("id")
    int id;
    @SerializedName("review")
    SentimentReview review;
    @SerializedName("presets")
    ArrayList<SentimentPreset> presets;

    public SentimentBody(int questionId, String comment, String image, String video, ArrayList<SentimentPreset> presets) {
        this.id = questionId;
        this.review = new SentimentReview(comment, image, video);
        this.presets = presets;
    }
}
