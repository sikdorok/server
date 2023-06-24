package com.ddd.chulsi.infrastructure.oauth;

public class OauthCommand {

    public record LoginCommand(
        String authorizationCode,
        String state
    ) {
        public static LoginCommand nonState(String authorizationCode) {
            return new LoginCommand(authorizationCode, null);
        }
    }

    public record LogoutCommand(
        String accessToken
    ) {

    }
}
