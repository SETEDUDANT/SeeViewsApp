package com.seeviews.model.api.send;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jan-Willem on 29-11-2016.
 */

public class UserBody {

    @SerializedName("grant_type")
    String grant_type = "password";
    @SerializedName("client_id")
    String client_id;
    @SerializedName("client_secret")
    String client_secret;
    @SerializedName("username")
    String username;
    @SerializedName("password")
    String password = "";
    @SerializedName("scope")
    String scope = "*";

    public UserBody(String clientId, String clientSecret, String username) {
        this.client_id = clientId;
        this.client_secret = clientSecret;
        this.username = username;
    }
}
