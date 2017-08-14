package com.seeviews.model.internal;

import com.google.gson.annotations.SerializedName;
import com.seeviews.model.api.receive.UserResponse;
import com.seeviews.utils.StringUtils;

/**
 * Created by Jan-Willem on 4-12-2016.
 */
public class User {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;

    public User(UserResponse user) {
        this.id = user.getId();
        this.name = user.getName();
    }

    public User() {
    }

    public String getName() {
        if (name == null)
            name = "";
        return name;
    }

    public boolean isValid() {
        return !StringUtils.hasEmpty(getName());
    }
}
