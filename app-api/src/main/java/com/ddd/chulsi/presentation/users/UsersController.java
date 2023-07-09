package com.ddd.chulsi.presentation.users;

import com.ddd.chulsi.application.users.UsersFacade;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import com.ddd.chulsi.presentation.users.dto.UsersDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping(value = "/kakao/login", name = "카카오 로그인")
    public BaseResponse<UsersDTO.LoginResponse> kakaoLogin(
        @RequestBody @Valid UsersDTO.OauthLoginRequest loginRequest,
        HttpServletResponse response
    ) {
        OauthCommand.LoginCommand loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.kakaoLogin(loginCommand, response));
    }

    @PutMapping(value = "/logout", name = "로그아웃")
    public BaseResponse<Void> logout(
        @AuthToken String token
    ) {
        usersFacade.logout(token);
        return BaseResponse.ofSuccess();
    }

}
