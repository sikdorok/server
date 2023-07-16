package com.ddd.chulsi.presentation.users.dto;

import com.ddd.chulsi.domainCore.model.users.UsersCommand;
import com.ddd.chulsi.domainCore.model.users.UsersInfo;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import com.ddd.chulsi.infrastructure.util.BCryptUtils;
import com.ddd.chulsi.infrastructure.util.StringUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;
import java.util.UUID;

public class UsersDTO {

    public record OauthLoginRequest(
        String authorizationCode
    ) {
        public OauthLoginRequest {
            Objects.requireNonNull(authorizationCode);
        }
        public UsersCommand.LoginCommand toCommand() {
            return UsersCommand.LoginCommand.nonState(authorizationCode);
        }
    }

    public record LoginResponse(
        UsersInfo.UsersInfoLogin usersInfoLogin
    ) {

    }

    public record Register(
        String nickname,
        String email,
        String password,
        String passwordCheck
    ) {
        public Register {
            if (StringUtils.isBlank(nickname))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "nickname");
            if (nickname.length() < 2 || nickname.length() > 10)
                throw new BadRequestException("이름은 2자 이상, 10자 이하로 입력해주세요", "nickname");

            if (StringUtils.isBlank(email) || !EmailValidator.getInstance().isValid(email))
                throw new BadRequestException(ErrorMessage.EMAIL_VALIDATION_FAILED, "email");

            if (StringUtils.isBlank(password))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "password");
            if (!StringUtil.isEnglishAndNumberAndSpecial(password, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "password");

            if (StringUtils.isBlank(passwordCheck))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "passwordCheck");
            if (!StringUtil.isEnglishAndNumberAndSpecial(passwordCheck, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "passwordCheck");

            if (!Objects.deepEquals(password, passwordCheck))
                throw new BadRequestException("입력하신 비밀번호가 일치하지 않습니다", "password, passwordCheck");
        }
        public UsersCommand.RegisterCommand toCommand() {
            return UsersCommand.RegisterCommand.nonState(nickname, email, BCryptUtils.hash(password));
        }
    }

    public record LoginRequest(
        String email,
        String password
    ) {
        public LoginRequest {
            if (StringUtils.isBlank(email) || !EmailValidator.getInstance().isValid(email))
                throw new BadRequestException(ErrorMessage.EMAIL_VALIDATION_FAILED, "email");
            if (StringUtils.isBlank(password))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "password");
            if (!StringUtil.isEnglishAndNumberAndSpecial(password, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "password");
        }
        public UsersCommand.UsersLogin toCommand() {
            return UsersCommand.UsersLogin.nonState(email, BCryptUtils.hash(password));
        }
    }

    public record PasswordFindRequest(
        String email
    ) {
        public PasswordFindRequest {
            if (StringUtils.isBlank(email) || !EmailValidator.getInstance().isValid(email))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "email");
        }
        public UsersCommand.PasswordFind toCommand() {
            return UsersCommand.PasswordFind.nonState(email);
        }
    }

    public record PasswordResetRequest(
        UUID usersId,
        String password,
        String passwordCheck
    ) {
        public PasswordResetRequest {
            if (usersId == null)
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "usersId");

            if (StringUtils.isBlank(password))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "password");
            if (!StringUtil.isEnglishAndNumberAndSpecial(password, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "password");

            if (StringUtils.isBlank(passwordCheck))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "passwordCheck");
            if (!StringUtil.isEnglishAndNumberAndSpecial(passwordCheck, 8))
                throw new BadRequestException(ErrorMessage.PASSWORD_VALIDATION_FAILED, "passwordCheck");

            if (!Objects.deepEquals(password, passwordCheck))
                throw new BadRequestException("입력하신 비밀번호가 일치하지 않습니다", "password, passwordCheck");
        }

        public UsersCommand.PasswordReset toCommand() {
            return UsersCommand.PasswordReset.nonState(usersId, BCryptUtils.hash(password));
        }
    }

    public record PasswordLinkAliveRequest(
        UUID usersId,
        String code
    ) {
        public PasswordLinkAliveRequest {
            if (usersId == null)
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "usersId");

            if (StringUtils.isBlank(code))
                throw new BadRequestException(ErrorMessage.FORBIDDEN);
        }

        public UsersCommand.PasswordLinkAlive toCommand() {
            return UsersCommand.PasswordLinkAlive.nonState(usersId, code);
        }
    }
}
