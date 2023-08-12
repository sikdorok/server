package com.ddd.chulsi.presentation.users;

import com.ddd.chulsi.application.users.UsersFacade;
import com.ddd.chulsi.domainCore.model.users.UsersCommand;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import com.ddd.chulsi.presentation.users.dto.UsersDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", name = "유저")
@RequiredArgsConstructor
public class UsersController {

    private final UsersFacade usersFacade;

    @GetMapping
    public String kakaoAuthCode(@RequestParam("code") String code) {
        return usersFacade.kakaoAccessToken(code);
    }

    @PostMapping(value = "/kakao/login", name = "카카오 로그인")
    public BaseResponse<UsersDTO.KakaoLoginResponse> kakaoLogin(
        @RequestBody @Valid UsersDTO.OauthLoginRequest loginRequest,
        HttpServletResponse response
    ) {
        UsersCommand.LoginCommand loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.kakaoLogin(loginCommand, response));
    }

    @PostMapping(value = "/logout", name = "로그아웃")
    public BaseResponse<Void> logout(
        @AuthToken String token
    ) {
        usersFacade.logout(token);
        return BaseResponse.ofSuccess();
    }

    @PostMapping(value = "/register", name = "회원가입")
    public BaseResponse<UsersDTO.LoginResponse> register(
        @RequestBody @Valid UsersDTO.Register register,
        HttpServletResponse response
    ) {
        UsersCommand.RegisterCommand registerCommand = register.toCommand();
        return BaseResponse.ofSuccess(usersFacade.register(registerCommand, response));
    }

    @PostMapping(value = "/login", name = "로그인")
    public BaseResponse<UsersDTO.LoginResponse> login(
        @RequestBody @Valid UsersDTO.LoginRequest loginRequest,
        HttpServletResponse response
    ) {
        UsersCommand.UsersLogin loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.login(loginCommand, response));
    }

    @PostMapping(value = "/auto-login", name = "자동 로그인")
    public BaseResponse<UsersDTO.LoginResponse> autoLogin(
        @AuthToken String token,
        HttpServletResponse response
    ) {
        return BaseResponse.ofSuccess(usersFacade.autoLogin(token, response));
    }

    @PostMapping(value = "/password-find", name = "비밀번호 찾기")
    public BaseResponse<Boolean> passwordFind(
        @RequestBody @Valid UsersDTO.PasswordFindRequest passwordFindRequest
    ) {
        UsersCommand.PasswordFind passwordFind = passwordFindRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.passwordFind(passwordFind));
    }

    @PostMapping(value = "/password-link-alive", name = "비밀번호 재설정 링크 유효성 검사")
    public BaseResponse<Boolean> passwordLinkAlive(
        @RequestBody @Valid UsersDTO.PasswordLinkAliveRequest passwordLinkAliveRequest
    ) {
        UsersCommand.PasswordLinkAlive passwordLinkAlive = passwordLinkAliveRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.passwordLinkAlive(passwordLinkAlive));
    }

    @PutMapping(value = "/password-reset", name = "비밀번호 재설정")
    public BaseResponse<Void> passwordReset(
        @RequestBody @Valid UsersDTO.PasswordResetRequest passwordResetRequest
    ) {
        UsersCommand.PasswordReset passwordReset = passwordResetRequest.toCommand();
        usersFacade.passwordReset(passwordReset);
        return BaseResponse.ofSuccess();
    }

    @GetMapping(value = "/email-check/{email}", name = "이메일 중복검사")
    public BaseResponse<Boolean> emailCheck(
        @PathVariable @Email @NotBlank String email
    ) {
        return BaseResponse.ofSuccess(usersFacade.emailCheck(email));
    }

    @PostMapping(value = "/access-token", name = "Access Token 재발급")
    public BaseResponse<String> accessToken(
        @RequestBody @Valid @NotBlank UsersDTO.AccessTokenRequest accessTokenRequest
    ) {
        String accessToken = usersFacade.accessToken(accessTokenRequest.refreshToken());
        return BaseResponse.ofSuccess(accessToken);
    }

    @DeleteMapping(name = "탈퇴")
    public BaseResponse<Void> revoke(
        @AuthToken String token
    ) {
        usersFacade.revoke(token);
        return BaseResponse.ofSuccess();
    }

}
