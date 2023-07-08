package com.ddd.chulsi.presentation.users;

import com.ddd.chulsi.application.users.UsersFacade;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import com.ddd.chulsi.presentation.users.dto.UsersDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", name = "유저")
@RequiredArgsConstructor
public class UsersController {

    private final UsersFacade usersFacade;

    // TODO : 클라로 변경 시 제거
    @GetMapping
    public String test(@RequestParam("code") String code) {
        return code;
    }

    // TODO : Users 엔티티 연결 후 내부에서 사용할 JWT 및 Users 정보 Response 로 교체 필요
    //        임시방편으로 현재 Kakao AccessToken 그대로 Return
    @PostMapping(value = "/oauth/kakao/login", name = "카카오 로그인")
    public BaseResponse<UsersDTO.LoginResponse> kakaoLogin(
        @RequestBody @Valid UsersDTO.OauthLoginRequest loginRequest,
        HttpServletResponse response
    ) {
        OauthCommand.LoginCommand loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.kakaoLogin(loginCommand, response));
    }

    // TODO : Users Logout 처리 기능 개발 시 이거 사용할 것
    @PutMapping(value = "/oauth/kakao/logout", name = "카카오 로그아웃")
    public BaseResponse<Void> kakaoLogout(
        @RequestHeader(name = HttpHeaders.AUTHORIZATION) String token
    ) {
        usersFacade.kakaoLogout(token);
        return BaseResponse.ofSuccess();
    }

}
