package com.ddd.chulsi.application.users;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.ddd.chulsi.domainCore.model.oauthToken.OauthToken;
import com.ddd.chulsi.domainCore.model.oauthToken.OauthTokenService;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.domainCore.model.users.Users;
import com.ddd.chulsi.domainCore.model.users.UsersCommand;
import com.ddd.chulsi.domainCore.model.users.UsersInfo;
import com.ddd.chulsi.domainCore.model.users.UsersService;
import com.ddd.chulsi.infrastructure.exception.UserNotFoundException;
import com.ddd.chulsi.infrastructure.jwt.JWTClaim;
import com.ddd.chulsi.infrastructure.jwt.JWTProperties;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.infrastructure.oauth.OauthCommand;
import com.ddd.chulsi.infrastructure.oauth.OauthInfo;
import com.ddd.chulsi.infrastructure.oauth.OauthKakaoService;
import com.ddd.chulsi.presentation.users.dto.UsersDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    private final UsersService usersService;
    private final OauthTokenService oauthTokenService;
    private final OauthKakaoService oauthKakaoService;

    @Transactional(readOnly = true)
    public UsersDTO.LoginResponse login(UsersCommand.UsersLogin usersLogin, HttpServletResponse response) {
        Users users = usersService.findByEmailAndPassword(usersLogin.email(), usersLogin.password());

        if (users == null) throw new UserNotFoundException();

        users.updateLastLoginAt();

        return usersLogin(new UsersInfo.UsersInfoLogin(users, null), response, null);
    }

    @Transactional(readOnly = true)
    public UsersDTO.LoginResponse autoLogin(String token, HttpServletResponse response) {
        JWTClaim jwtClaim = jwtTokenUtil.getClaims(token, properties, true);

        UUID usersId = jwtClaim.getUserId();
        Users users = usersService.findByUsersId(usersId);
        if (users == null) throw new UserNotFoundException();

        users.updateLastLoginAt();

        return usersLogin(new UsersInfo.UsersInfoLogin(users, null), response, token);
    }

    public UsersDTO.LoginResponse usersLogin(UsersInfo.UsersInfoLogin usersInfoLogin, HttpServletResponse response, String prevAccessToken) {
        JWTClaim jwtClaim = JWTClaim.builder()
            .userId(usersInfoLogin.getUsersId())
            .auth(DefinedCode.C000100002)
            .build();

        // Token 발행
        String accessToken = prevAccessToken == null ? jwtTokenUtil.createToken(jwtClaim, properties, true) : prevAccessToken;
        String refreshToken = usersInfoLogin.getRefreshToken() == null ? jwtTokenUtil.createToken(jwtClaim, properties, false) : usersInfoLogin.getRefreshToken();

        try {
            jwtTokenUtil.getClaimsForRefreshToken(refreshToken, properties, true);
        } catch (TokenExpiredException e) {
            refreshToken = jwtTokenUtil.createToken(jwtClaim, properties, false);
        }

        // Token 정보 추가
        usersInfoLogin.setAccessToken(accessToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
            .maxAge((long) 14 * 24 * 60 * 60)
            .path("/")
            .secure(true)
            .httpOnly(true)
            .sameSite("None")
            .build();
        response.setHeader("Set-Cookie", cookie.toString());

        eventPublisher.publishEvent(new UsersCommand.UsersRefreshTokenUpdateEvent(usersInfoLogin.getUsersId(), refreshToken));

        return new UsersDTO.LoginResponse(usersInfoLogin);
    }

    @EventListener
    @Transactional(rollbackFor = Exception.class)
    public void updateRefreshToken(UsersCommand.UsersRefreshTokenUpdateEvent event) {
        Users users = usersService.findByUsersId(event.usersId());
        users.updateRefreshToken(event.refreshToken());
    }


    @Transactional(rollbackFor = Exception.class)
    public UsersDTO.LoginResponse kakaoLogin(OauthCommand.LoginCommand loginCommand, HttpServletResponse response) {
        OauthInfo.KakaoInfoResponse kakaoInfoResponse = oauthKakaoService.getAccessToken(loginCommand.authorizationCode());
        OauthInfo.KakaoUserMe kakaoUserMe = oauthKakaoService.getUserName(kakaoInfoResponse.accessToken());

        Users users = usersService.findByOauthTypeAndOauthId(DefinedCode.C000200001, kakaoUserMe.id());
        if (users == null) {
            // 존재하지 않는 회원이면 회원가입 처리
            users = usersService.store(
                Users.builder()
                    .oauthType(DefinedCode.C000200001)
                    .oauthId(kakaoUserMe.id())
                    .name(kakaoUserMe.kakaoAccount().kakaoProfile().nickname())
                    .build()
            );
        }

        users.updateLastLoginAt();

        oauthTokenService.save(OauthToken.builder().oauthId(kakaoUserMe.id()).oauthType(DefinedCode.C000200001).accessToken(kakaoInfoResponse.accessToken()).build());

        return usersLogin(new UsersInfo.UsersInfoLogin(users, kakaoInfoResponse.accessToken()), response, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void logout(String token) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        Users users = usersService.findByUsersId(jwtClaim.getUserId());
        if (users == null) throw new UserNotFoundException();

        if (users.getOauthType() == DefinedCode.C000200001) {
            OauthToken oauthToken = oauthTokenService.findByOauthTypeAndOauthId(users.getOauthType(), users.getOauthId());
            if (oauthToken != null) {
                oauthKakaoService.logout(oauthToken.getAccessToken());
                oauthTokenService.delete(oauthToken);
            }
        }

        users.logout();
    }

}
