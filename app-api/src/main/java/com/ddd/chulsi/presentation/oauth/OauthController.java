package com.ddd.chulsi.presentation.oauth;

import com.ddd.chulsi.application.oauth.OauthFacade;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.presentation.oauth.dto.OauthDTO;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/oauth", name = "oauth 2.0")
@RequiredArgsConstructor
public class OauthController {

    private final OauthFacade oauthFacade;

    // TODO : 클라로 변경 시 제거
    @GetMapping
    public String test(@RequestParam("code") String code) {
        return code;
    }

    // TODO : Users 엔티티 연결 후 내부에서 사용할 JWT 및 Users 정보 Response 로 교체 필요
    //        임시방편으로 현재 Kakao AccessToken 그대로 Return
    @PostMapping(value = "/kakao/login", name = "카카오 로그인")
    public BaseResponse<OauthDTO.LoginResponse> kakaoLogin(
        @RequestBody @Valid OauthDTO.LoginRequest loginRequest
    ) {
        OauthCommand.LoginCommand loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(oauthFacade.kakaoLogin(loginCommand));
    }

    // TODO : Users Logout 처리 기능 개발 시 이거 사용할 것
    @PutMapping(value = "/kakao/logout", name = "카카오 로그아웃")
    public BaseResponse<Void> kakaoLogout(
        @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token
    ) {
        oauthFacade.kakaoLogout(token);
        return BaseResponse.ofSuccess();
    }

}
