package com.ddd.chulsi.infrastructure.oauth;

public interface OauthKakaoService {

    String getAccessToken(String authorizationCode);

    void logout(String accessToken);

}
