package com.seeviews.model.api.send;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan-Willem on 14-12-2016.
 */

public class SentimentReview {

    @SerializedName("comment")
    String comment;
    @SerializedName("image")
    String image;
    @SerializedName("video")
    String video;

    public SentimentReview(String comment, String image, String video) {
        this.comment = comment;
        this.image = image;
        this.video = video;
    }
}
