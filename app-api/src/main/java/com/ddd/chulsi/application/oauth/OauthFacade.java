package com.ddd.chulsi.application.oauth;

import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.infrastructure.oauth.OauthInfo;
import com.ddd.chulsi.infrastructure.oauth.OauthKakaoService;
import com.ddd.chulsi.presentation.oauth.dto.OauthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OauthFacade {

    private final OauthKakaoService oauthKakaoService;

    public OauthDTO.LoginResponse kakaoLogin(OauthCommand.LoginCommand loginCommand) {
        OauthInfo.KakaoInfoResponse kakaoInfoResponse = oauthKakaoService.getAccessToken(loginCommand.authorizationCode());
        OauthInfo.KakaoUserMe kakaoUserMe = oauthKakaoService.getUserName(kakaoInfoResponse.accessToken());
        return new OauthDTO.LoginResponse(kakaoInfoResponse.accessToken());
    }

    public void kakaoLogout(String token) {
        oauthKakaoService.logout(token);
    }

}
