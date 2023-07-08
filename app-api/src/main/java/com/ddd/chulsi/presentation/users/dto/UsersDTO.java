package com.ddd.chulsi.presentation.users.dto;

import com.ddd.chulsi.domainCore.model.users.UsersInfo;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;

import java.util.Objects;

public class UsersDTO {

    public record OauthLoginRequest(
        String authorizationCode
    ) {
        public OauthLoginRequest {
            Objects.requireNonNull(authorizationCode);
        }
        public OauthCommand.LoginCommand toCommand() {
            return OauthCommand.LoginCommand.nonState(authorizationCode);
        }
    }

    public record LoginResponse(
        UsersInfo.UsersInfoLogin usersInfoLogin
    ) {

    }

}
