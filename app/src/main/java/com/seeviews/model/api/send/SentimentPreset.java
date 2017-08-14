package com.seeviews.model.api.send;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan-Willem on 14-12-2016.
 */

public class SentimentPreset {

    @SerializedName("id")
    int id;
    @SerializedName("value")
    float value;

    public SentimentPreset(int id, float value) {
        this.id = id;
        this.value = value;
    }

    public SentimentPreset(Integer id, Integer value) {
        this.id = id;
        this.value = value;
    }
}
