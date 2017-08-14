package com.seeviews.model.api.receive;

import com.google.gson.annotations.SerializedName;
import com.seeviews.utils.StringUtils;

/**
 * Created by Jan-Willem on 29-11-2016.
 */

public class OAuthResponse {

    @SerializedName("token_type")
    String token_type;
    @SerializedName("expires_in")
    long expires_in; //in seconds
    @SerializedName("access_token")
    String access_token;
    @SerializedName("refresh_token")
    String refresh_token;

    protected void copy(OAuthResponse resonse) {
        this.token_type = resonse.token_type;
        this.expires_in = resonse.expires_in;
        this.access_token = resonse.access_token;
        this.refresh_token = resonse.refresh_token;
    }

    public String getTokenType() {
        if (token_type == null)
            token_type = "";
        return token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getAccess_token() {
        if (access_token == null)
            access_token = "";
        return access_token;
    }

    public String getRefresh_token() {
        if (refresh_token == null)
            refresh_token = "";
        return refresh_token;
    }

    public boolean isValid() {
        return !StringUtils.hasEmpty(getTokenType(), getAccess_token(), getRefresh_token()) && getExpires_in() > 0;
    }
}
