package com.sikdorok.appapi.infrastructure.inMemory.users;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.domaincore.model.users.Users;
import com.sikdorok.domaincore.model.users.UsersInfo;
import com.sikdorok.appapi.infrastructure.oauth.OauthInfo;
import com.sikdorok.appapi.presentation.users.dto.UsersDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsersFactory {

    public static Users givenUsers() {
        return Users.builder()
            .usersId(UUID.randomUUID())
            .oauthId(2_384_324L)
            .oauthType(DefinedCode.C000200001)
            .email("email@email.com")
            .password("qwer1234!")
            .name("이름")
            .refreshToken("refreshToken")
            .lastLoginAt(LocalDateTime.now())
            .level(1)
            .points(0)
            .photosLimit(20)
            .build();
    }

    public static UsersDTO.LoginResponse givenLoginResponse() {
        UsersInfo.UsersInfoLogin usersInfoLogin = new UsersInfo.UsersInfoLogin(givenUsers(), "oauth access token");
        usersInfoLogin.setAccessToken("access Token");
        return new UsersDTO.LoginResponse(true, usersInfoLogin);
    }

    public static UsersDTO.KakaoLoginResponse<UsersInfo.UsersInfoLogin> givenKakaoLoginResponse() {
        UsersInfo.UsersInfoLogin usersInfoLogin = new UsersInfo.UsersInfoLogin(givenUsers(), "oauth access token");
        usersInfoLogin.setAccessToken("access Token");
        return new UsersDTO.KakaoLoginResponse<>(true, usersInfoLogin);
    }

    public static UsersDTO.KakaoLoginResponse<OauthInfo.KakaoUserMeDTO> givenKakaoLoginNeedSignUpResponse() {
        OauthInfo.KakaoUserMeDTO kakaoUserMeDTO = new OauthInfo.KakaoUserMeDTO(DefinedCode.C000200001, 1_123_123L, "닉네임", "team.sikdorok@gmail.com", true);
        return new UsersDTO.KakaoLoginResponse<>(false, kakaoUserMeDTO);
    }

    public static UsersDTO.LoginRequest givenLoginRequest() {
        return new UsersDTO.LoginRequest(
            "team.sikdorok@gmail.com",
            "qwer1234!"
        );
    }

    public static UsersDTO.Register givenRegisterRequest() {
        return new UsersDTO.Register(
            DefinedCode.C000200001,
            1_123_123L,
            "닉네임",
            "team.sikdorok@gmail.com",
            "qwer1234!",
            "qwer1234!"
        );
    }

    public static UsersDTO.SettingsResponse givenSettingsResponse() {
        return new UsersDTO.SettingsResponse(
            DefinedCode.C000200001,
            "닉네임",
            "team.sikdorok@gmail.com",
            true
        );
    }

    public static UsersDTO.ProfileResponse givenProfileResponse() {
        return new UsersDTO.ProfileResponse(
            "닉네임",
            "email@test.com"
        );
    }

}
