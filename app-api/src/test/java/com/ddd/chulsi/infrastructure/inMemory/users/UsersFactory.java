package com.ddd.chulsi.infrastructure.inMemory.users;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.domainCore.model.users.Users;
import com.ddd.chulsi.domainCore.model.users.UsersInfo;
import com.ddd.chulsi.presentation.users.dto.UsersDTO;

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

    public static UsersDTO.KakaoLoginResponse givenKakaoLoginResponse() {
        UsersInfo.UsersInfoLogin usersInfoLogin = new UsersInfo.UsersInfoLogin(givenUsers(), "oauth access token");
        usersInfoLogin.setAccessToken("access Token");
        return new UsersDTO.KakaoLoginResponse(true, usersInfoLogin);
    }

    public static UsersDTO.LoginRequest givenLoginRequest() {
        return new UsersDTO.LoginRequest(
            "sikdorok@chulsi.com",
            "qwer1234!"
        );
    }

    public static UsersDTO.Register givenRegisterRequest() {
        return new UsersDTO.Register(
            "닉네임",
            "sikdorok@chulsi.com",
            "qwer1234!",
            "qwer1234!"
        );
    }

}
