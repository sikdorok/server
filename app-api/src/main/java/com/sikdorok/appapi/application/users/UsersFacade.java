package com.sikdorok.appapi.application.users;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.sikdorok.domaincore.model.appVersion.AppVersionInfo;
import com.sikdorok.domaincore.model.appVersion.AppVersionService;
import com.sikdorok.domaincore.model.feed.FeedCommand;
import com.sikdorok.domaincore.model.oauthToken.OauthToken;
import com.sikdorok.domaincore.model.oauthToken.OauthTokenService;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.domaincore.model.users.Users;
import com.sikdorok.domaincore.model.users.UsersCommand;
import com.sikdorok.domaincore.model.users.UsersInfo;
import com.sikdorok.domaincore.model.users.UsersService;
import com.sikdorok.appapi.infrastructure.exception.*;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import com.sikdorok.appapi.infrastructure.jwt.JWTClaim;
import com.sikdorok.appapi.infrastructure.jwt.JWTProperties;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.infrastructure.mail.MailService;
import com.sikdorok.appapi.infrastructure.oauth.OauthInfo;
import com.sikdorok.appapi.infrastructure.oauth.OauthKakaoService;
import com.sikdorok.appapi.infrastructure.specification.users.UsersSpecification;
import com.sikdorok.appapi.infrastructure.util.RedisUtil;
import com.sikdorok.system.StringUtil;
import com.sikdorok.appapi.presentation.users.dto.UsersDTO;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    private final RedisUtil redisUtil;

    private final UsersService usersService;
    private final OauthTokenService oauthTokenService;
    private final OauthKakaoService oauthKakaoService;
    private final MailService mailService;
    private final UsersSpecification usersSpecification;
    private final AppVersionService appVersionService;

    @Transactional(rollbackFor = Exception.class)
    public UsersDTO.LoginResponse login(UsersCommand.UsersLogin usersLogin) {
        Users users = usersService.findByEmailAndPassword(usersLogin.email(), usersLogin.password());

        if (users == null) throw new UserNotFoundException();

        return usersLogin(users, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public UsersDTO.LoginResponse autoLogin(String token) {
        JWTClaim jwtClaim = jwtTokenUtil.getClaims(token, properties, true);

        UUID usersId = jwtClaim.getUsersId();
        Users users = usersService.findByUsersId(usersId);
        if (
            users == null ||
                StringUtils.isBlank(users.getRefreshToken()) ||
                !users.getRefreshToken().equals(token)
        )
            throw new UserNotFoundException();

        return usersLogin(users, null);
    }

    public UsersDTO.LoginResponse usersLogin(Users users, String oauthAccessToken) {
        UsersInfo.UsersInfoLogin usersInfoLogin = new UsersInfo.UsersInfoLogin(users, oauthAccessToken);

        JWTClaim jwtClaim = JWTClaim.builder()
            .usersId(usersInfoLogin.getUsersId())
            .auth(usersInfoLogin.getAuth())
            .build();

        // Token 발행
        String accessToken = jwtTokenUtil.createToken(jwtClaim, properties, true);
        String refreshToken = usersInfoLogin.getRefreshToken() == null ? jwtTokenUtil.createToken(jwtClaim, properties, false) : usersInfoLogin.getRefreshToken();

        try {
            jwtTokenUtil.getClaimsForRefreshToken(refreshToken, properties, true);
        } catch (TokenExpiredException e) {
            refreshToken = jwtTokenUtil.createToken(jwtClaim, properties, false);
        }

        // Token 정보 추가
        usersInfoLogin.setAccessToken(accessToken);
        usersInfoLogin.setRefreshToken(refreshToken);

        // Refresh Token 유효시간을 가져온 후 Redis에 Refresh Token을 저장합니다.
        redisUtil.set("REFRESH_TOKEN:" + usersInfoLogin.getUsersId(), refreshToken, properties.getRefreshExpiresTime(), TimeUnit.MILLISECONDS);

        users.updateRefreshToken(refreshToken);
        users.updateLastLoginAt();

        return new UsersDTO.LoginResponse(true, usersInfoLogin);
    }

    public String kakaoAccessToken(String code) {
        OauthInfo.KakaoInfoResponse kakaoInfoResponse = oauthKakaoService.getAccessToken(code);
        if (kakaoInfoResponse == null) throw new OauthException(700, ErrorMessage.OAUTH_REQUEST_FAILED);
        return kakaoInfoResponse.accessToken();
    }


    @Transactional(rollbackFor = Exception.class)
    public UsersDTO.KakaoLoginResponse kakaoLogin(UsersCommand.LoginCommand loginCommand) {
        OauthInfo.KakaoUserMe kakaoUserMe = oauthKakaoService.getUserName(loginCommand.accessToken());

        String nickname = kakaoUserMe.kakaoAccount().kakaoProfile().nickname();
        String email = kakaoUserMe.kakaoAccount().email();
        boolean isValidEmail = kakaoUserMe.kakaoAccount().isEmailValid() && kakaoUserMe.kakaoAccount().isEmailVerified();
        OauthInfo.KakaoUserMeDTO kakaoUserMeDTO = new OauthInfo.KakaoUserMeDTO(DefinedCode.C000200001, kakaoUserMe.id(), nickname, email, isValidEmail);

        Users users = usersService.findByOauthTypeAndOauthId(DefinedCode.C000200001, kakaoUserMe.id());
        if (users == null) {
            // 존재하지 않는 회원이면 회원가입 처리를 위해 데이터 Return
            boolean isRegistered = false;

            // 이메일 중복검사
            if (email != null) {
                Users duplicationCheckByEmail = usersService.findByEmail(email);
                if (duplicationCheckByEmail != null) {
                    kakaoUserMeDTO.setValidEmail(false);
                    isRegistered = true;
                }
            }

            return new UsersDTO.KakaoLoginResponse<>(isRegistered, kakaoUserMeDTO);
        }

        OauthToken oauthToken = oauthTokenService.findByOauthTypeAndOauthId(DefinedCode.C000200001, kakaoUserMe.id());
        if (oauthToken == null) {
            oauthTokenService.save(OauthToken.builder().oauthId(kakaoUserMe.id()).oauthType(DefinedCode.C000200001).accessToken(loginCommand.accessToken()).build());
        } else {
            oauthToken.updateAccessToken(loginCommand.accessToken());
        }

        UsersDTO.LoginResponse loginResponse = usersLogin(users, loginCommand.accessToken());

        return new UsersDTO.KakaoLoginResponse<>(
            true,
            loginResponse.usersInfo()
        );
    }

    @Transactional(rollbackFor = Exception.class, noRollbackFor = SlackNotificationHandler.class)
    public void logout(String token) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        Users users = usersService.findByUsersId(jwtClaim.getUsersId());
        if (users == null) throw new UserNotFoundException();

        if (users.getOauthType() == DefinedCode.C000200001) {
            OauthToken oauthToken = oauthTokenService.findByOauthTypeAndOauthId(users.getOauthType(), users.getOauthId());
            if (oauthToken != null) {
                oauthKakaoService.logout(oauthToken.getAccessToken());
                oauthTokenService.delete(oauthToken);
            }
        }

        users.logout();

        // Redis 에서 해당 User id 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        try {
            if (redisUtil.hasKey("REFRESH_TOKEN:" + users.getUsersId()).get()) {
                redisUtil.delete("REFRESH_TOKEN:" + users.getUsersId());

                Long expiration = jwtTokenUtil.getExpiration(token);
                redisUtil.set(token, "logout", expiration, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new SlackNotificationHandler("Redis Server Error");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public UsersDTO.LoginResponse register(UsersCommand.RegisterCommand registerCommand) {
        // 이메일 중복검사
        if (usersService.duplicationCheckEmail(registerCommand.email()))
            throw new UserExistsException();

        // 회원가입 처리
        Users insertUsers = registerCommand.toEntity();
        Users users = usersService.store(insertUsers);

        // 로그인 처리
        return usersLogin(users, null);
    }

    @Transactional(readOnly = true)
    public boolean passwordFind(UsersCommand.PasswordFind passwordFind) {
        Users users = usersService.findByEmail(passwordFind.email());
        if (users == null) throw new NotFoundException();

        String code = StringUtil.getRandomNumberString();

        try {
            if (redisUtil.hasKey("PASSWORD_LINK_ALIVE:" + users.getUsersId()).get()) {
                CompletableFuture<Object> future = redisUtil.get("PASSWORD_LINK_ALIVE:" + users.getUsersId());
                String codeCheck = future.get().toString();

                while (code.equals(codeCheck))
                    code = StringUtil.getRandomNumberString();

                redisUtil.delete("PASSWORD_LINK_ALIVE:" + users.getUsersId());
            }

            redisUtil.set("PASSWORD_LINK_ALIVE:" + users.getUsersId(), code, 3600, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new SlackNotificationHandler("Redis Server Error");
        }

        return mailService.sendMail(Collections.singletonList(users), code);
    }

    @Transactional(rollbackFor = Exception.class)
    public void passwordReset(UsersCommand.PasswordReset passwordReset) {
        Users users = usersService.findByUsersId(passwordReset.usersId());
        if (users == null) throw new NotFoundException();

        users.passwordReset(passwordReset.password());

        try {
            if (redisUtil.hasKey("PASSWORD_LINK_ALIVE:" + users.getUsersId()).get())
                redisUtil.delete("PASSWORD_LINK_ALIVE:" + users.getUsersId());
        } catch (InterruptedException | ExecutionException e) {
            throw new SlackNotificationHandler("Redis Server Error");
        }
    }

    @Transactional(readOnly = true)
    public boolean passwordLinkAlive(UsersCommand.PasswordLinkAlive passwordLinkAlive) {
        Users users = usersService.findByUsersId(passwordLinkAlive.usersId());
        if (users == null) return false;

        try {
            if (redisUtil.hasKey("PASSWORD_LINK_ALIVE:" + users.getUsersId()).get()) {
                CompletableFuture<Object> future = redisUtil.get("PASSWORD_LINK_ALIVE:" + users.getUsersId());
                String code = future.get().toString();
                return code.equals(passwordLinkAlive.code());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new SlackNotificationHandler("Redis Server Error");
        }

        return false;
    }

    @Transactional(readOnly = true)
    public Boolean emailCheck(String email) {
        return usersService.duplicationCheckEmailNonLock(email);
    }

    @Transactional(readOnly = true)
    public String accessToken(String refreshToken) {
        JWTClaim jwtClaim = jwtTokenUtil.getClaimsForRefreshToken(refreshToken, properties, true);

        UUID userId = jwtClaim.getUsersId();

        // 존재하는 유저인지 확인
        Users users = usersService.findByUsersId(userId);
        if (users == null) return null;

        return jwtTokenUtil.createToken(jwtClaim, properties, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public void revoke(String token) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();
        Users users = usersSpecification.findByUsersId(usersId);

        // 피드 전체 삭제
        eventPublisher.publishEvent(new FeedCommand.RevokeUsers(usersId));

        usersService.revoke(users);
    }

    @Transactional(readOnly = true)
    public UsersDTO.SettingsResponse settings(String token, String version, DefinedCode deviceType) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();
        Users users = usersSpecification.findByUsersId(usersId);

        AppVersionInfo.AppVersionDTO latest = appVersionService.latest(deviceType);

        return new UsersDTO.SettingsResponse(
            users.getOauthType(),
            users.getName(),
            users.getEmail(),
            latest != null && StringUtils.isNotBlank(latest.appInfoAppVersion()) && latest.appInfoAppVersion().equals(version)
        );
    }

    @Transactional(readOnly = true)
    public UsersDTO.ProfileResponse profile(String token) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();
        Users users = usersSpecification.findByUsersId(usersId);

        return new UsersDTO.ProfileResponse(
            users.getName(),
            users.getEmail()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void profileUpdate(String token, UsersCommand.Profile profile) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);
        usersSpecification.findByUsersId(jwtClaim.getUsersId()).updateNickName(profile.nickname());
    }

}
