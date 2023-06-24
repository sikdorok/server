package com.ddd.chulsi.presentation.oauth.dto;

import com.ddd.chulsi.infrastructure.oauth.OauthCommand;

import java.util.Objects;

public class OauthDTO {

    public record LoginRequest(
        String authorizationCode
    ) {
        public LoginRequest {
            Objects.requireNonNull(authorizationCode);
        }
        public OauthCommand.LoginCommand toCommand() {
            return OauthCommand.LoginCommand.nonState(authorizationCode);
        }
    }

    public record LoginResponse(
        String accessToken
    ) {

    }

}
