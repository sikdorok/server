package com.ddd.chulsi.infrastructure.inMemory.oauth;

import com.ddd.chulsi.presentation.oauth.dto.OauthDTO;

public class OauthFactory {

    public static OauthDTO.LoginResponse givenLoginResponse() {
        return new OauthDTO.LoginResponse("accessToken");
    }

}
