package com.sikdorok.appapi.infrastructure.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.*;

public class OauthInfo {

    public record KakaoInfoResponse (
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

    }

    public record KakaoLogoutResponse (
        @JsonProperty("id")
        Long id
    ) {

    }

    public record KakaoUserMe (
        @JsonProperty("id")
        Long id,

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
    ) {
        public record KakaoAccount (
            @JsonProperty("profile")
            KakaoProfile kakaoProfile,

            @JsonProperty("is_email_valid")
            boolean isEmailValid,

            @JsonProperty("is_email_verified")
            boolean isEmailVerified,

            @JsonProperty("email")
            String email
        ) { }

        public record KakaoProfile (
            @JsonProperty("nickname")
            String nickname
        ) { }
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoUserMeDTO {
        private DefinedCode oauthType;
        private long oauthId;
        private String nickname;
        private String email;
        private boolean isValidEmail;
    }
}
