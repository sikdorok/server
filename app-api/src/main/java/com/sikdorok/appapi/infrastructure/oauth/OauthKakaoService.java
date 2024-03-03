package com.sikdorok.appapi.infrastructure.oauth;

public interface OauthKakaoService {

    OauthInfo.KakaoInfoResponse getAccessToken(String authorizationCode);

    void logout(String accessToken);

    OauthInfo.KakaoUserMe getUserName(String accessToken);

}
