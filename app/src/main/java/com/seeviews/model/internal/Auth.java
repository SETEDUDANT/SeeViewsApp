package com.seeviews.model.internal;

import com.google.gson.annotations.SerializedName;
import com.seeviews.model.api.receive.OAuthResponse;
import com.seeviews.utils.StringUtils;

/**
 * Created by Jan-Willem on 3-12-2016.
 */

public class Auth extends OAuthResponse {

    @SerializedName("validUntilS")
    long validUntilS; // in seconds;

    public Auth() {

    }

    public Auth(OAuthResponse authResponse) {
        copy(authResponse);
        this.validUntilS = currentTimeInSeconds() + authResponse.getExpires_in();
    }

    public long getValidUntilS() {
        return validUntilS;
    }

    private static long currentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    @Override
    public boolean isValid() {
        return !StringUtils.hasEmpty(getTokenType(), getAccess_token(), getRefresh_token())
                && getValidUntilS() > currentTimeInSeconds();
    }
}
