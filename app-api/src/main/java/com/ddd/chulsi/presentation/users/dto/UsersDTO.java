package com.ddd.chulsi.presentation.users.dto;

import com.ddd.chulsi.domainCore.model.users.UsersCommand;
import com.ddd.chulsi.domainCore.model.users.UsersInfo;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import com.ddd.chulsi.infrastructure.util.BCryptUtils;
import com.ddd.chulsi.infrastructure.util.StringUtil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class UsersDTO {

    public record OauthLoginRequest (
        @NotNull
        String authorizationCode
    ) {
        public UsersCommand.LoginCommand toCommand() {
            return UsersCommand.LoginCommand.nonState(authorizationCode);
        }
    }

    public record LoginResponse (
        boolean isRegistered,
        UsersInfo.UsersInfoLogin usersInfo
    ) {

    }

    public record KakaoLoginResponse<T> (
        boolean isRegistered,
        T usersInfo
    ) {

    }

    public record Register (
        @NotBlank
        String nickname,

        @Email(message = ErrorMessage.EMAIL_VALIDATION_FAILED)
        @NotBlank
        String email,

        @NotBlank
        String password,

        @NotBlank
        String passwordCheck
    ) {
        public Register {
            if (nickname.length() < 2 || nickname.length() > 10)
                throw new BadRequestException("이름은 2자 이상, 10자 이하로 입력해주세요", "nickname");
            if (!StringUtil.isEnglishAndNumberAndSpecial(password, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "password");
            if (!StringUtil.isEnglishAndNumberAndSpecial(passwordCheck, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "passwordCheck");
            if (!Objects.deepEquals(password, passwordCheck))
                throw new BadRequestException("입력하신 비밀번호가 일치하지 않습니다", "password, passwordCheck");
        }
        public UsersCommand.RegisterCommand toCommand() {
            return UsersCommand.RegisterCommand.nonState(nickname, email, BCryptUtils.hash(password));
        }
    }

    public record LoginRequest (
        @Email(message = ErrorMessage.EMAIL_VALIDATION_FAILED)
        @NotBlank
        String email,

        @NotBlank
        String password
    ) {
        public LoginRequest {
            if (!StringUtil.isEnglishAndNumberAndSpecial(password, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "password");
        }
        public UsersCommand.UsersLogin toCommand() {
            return UsersCommand.UsersLogin.nonState(email, BCryptUtils.hash(password));
        }
    }

    public record PasswordFindRequest (
        @Email(message = ErrorMessage.EMAIL_VALIDATION_FAILED)
        @NotBlank
        String email
    ) {
        public UsersCommand.PasswordFind toCommand() {
            return UsersCommand.PasswordFind.nonState(email);
        }
    }

    public record PasswordResetRequest (
        @NotNull
        UUID usersId,

        @NotBlank
        String password,

        @NotBlank
        String passwordCheck
    ) {
        public PasswordResetRequest {
            if (!StringUtil.isEnglishAndNumberAndSpecial(password, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "password");
            if (!StringUtil.isEnglishAndNumberAndSpecial(passwordCheck, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "passwordCheck");
            if (!Objects.deepEquals(password, passwordCheck))
                throw new BadRequestException("입력하신 비밀번호가 일치하지 않습니다", "password, passwordCheck");
        }

        public UsersCommand.PasswordReset toCommand() {
            return UsersCommand.PasswordReset.nonState(usersId, BCryptUtils.hash(password));
        }
    }

    public record PasswordLinkAliveRequest (
        @NotNull
        UUID usersId,

        @NotBlank
        String code
    ) {

        public UsersCommand.PasswordLinkAlive toCommand() {
            return UsersCommand.PasswordLinkAlive.nonState(usersId, code);
        }
    }
}
