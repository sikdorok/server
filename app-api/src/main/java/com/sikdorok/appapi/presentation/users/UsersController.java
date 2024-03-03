package com.sikdorok.appapi.presentation.users;

import com.sikdorok.appapi.application.users.UsersFacade;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.domaincore.model.users.UsersCommand;
import com.sikdorok.appapi.infrastructure.annotation.AuthToken;
import com.sikdorok.appapi.presentation.shared.response.dto.BaseResponse;
import com.sikdorok.appapi.presentation.users.dto.UsersDTO;
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
        @RequestBody @Valid UsersDTO.OauthLoginRequest loginRequest
    ) {
        UsersCommand.LoginCommand loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.kakaoLogin(loginCommand));
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
        @RequestBody @Valid UsersDTO.Register register
    ) {
        UsersCommand.RegisterCommand registerCommand = register.toCommand();
        return BaseResponse.ofSuccess(usersFacade.register(registerCommand));
    }

    @PostMapping(value = "/login", name = "로그인")
    public BaseResponse<UsersDTO.LoginResponse> login(
        @RequestBody @Valid UsersDTO.LoginRequest loginRequest
    ) {
        UsersCommand.UsersLogin loginCommand = loginRequest.toCommand();
        return BaseResponse.ofSuccess(usersFacade.login(loginCommand));
    }

    @PostMapping(value = "/auto-login", name = "자동 로그인")
    public BaseResponse<UsersDTO.LoginResponse> autoLogin(
        @AuthToken String token
    ) {
        return BaseResponse.ofSuccess(usersFacade.autoLogin(token));
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

    @GetMapping(value = "/settings", name = "설정")
    public BaseResponse<UsersDTO.SettingsResponse> settings(
        @AuthToken String token,
        @RequestParam String version,
        @RequestParam DefinedCode deviceType
    ) {
        return BaseResponse.ofSuccess(usersFacade.settings(token, version, deviceType));
    }

    @GetMapping(value = "/profile", name = "프로필 관리")
    public BaseResponse<UsersDTO.ProfileResponse> profile(
        @AuthToken String token
    ) {
        return BaseResponse.ofSuccess(usersFacade.profile(token));
    }

    @PutMapping(value = "/profile", name = "프로필 관리 수정")
    public BaseResponse<Void> profileUpdate(
        @AuthToken String token,
        @RequestBody @Valid UsersDTO.ProfileRequest profileRequest
    ) {
        UsersCommand.Profile initProfile = profileRequest.toCommand();
        usersFacade.profileUpdate(token, initProfile);
        return BaseResponse.ofSuccess();
    }

}
