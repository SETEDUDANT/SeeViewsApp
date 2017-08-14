package com.seeviews.model.api.receive;

import com.google.gson.annotations.SerializedName;
import com.seeviews.utils.StringUtils;

/**
 * Created by Jan-Willem on 16-12-2016.
 */
public class Incentive {

    @SerializedName("title")
    String title;
    @SerializedName("description")
    String description;
    @SerializedName("image")
    ImageName image;

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

    public String getImage() {
        if (image == null)
            image = new ImageName();
        return image.getName();
    }

    public boolean hasContentToShow() {
        return StringUtils.isNotEmpty(getTitle())
                || StringUtils.isNotEmpty(getDescription())
                || StringUtils.isNotEmpty(getImage());
    }
}
