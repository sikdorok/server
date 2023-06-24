package com.ddd.chulsi.infrastructure.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OauthInfo {

    public record KakaoInfoResponse(
        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        Integer expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_token_expires_in")
        Integer refreshTokenExpiresIn
    ) {
        @Override
        public String toString() {
            return "{"
                + "\"tokenType\":\"" + tokenType + "\""
                + ", \"accessToken\":\"" + accessToken + "\""
                + ", \"expiresIn\":" + expiresIn
                + ", \"refreshToken\":\"" + refreshToken + "\""
                + ", \"refreshTokenExpiresIn\":" + refreshTokenExpiresIn
                + "}";
        }
    }

    public record KakaoLogoutResponse(
        @JsonProperty("id")
        Long id
    ) {

    }

}
