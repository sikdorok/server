package com.ddd.chulsi.presentation.users.dto;

import com.ddd.chulsi.domainCore.model.users.UsersCommand;
import com.ddd.chulsi.domainCore.model.users.UsersInfo;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import com.ddd.chulsi.infrastructure.util.BCryptUtils;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

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
            if (StringUtils.isBlank(email) || !EmailValidator.getInstance().isValid(email))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "email");
            if (StringUtils.isBlank(password))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "password");
            if (StringUtils.isBlank(passwordCheck))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "passwordCheck");
            if (!Objects.deepEquals(password, passwordCheck))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "password, passwordCheck");
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
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "email");
            if (StringUtils.isBlank(password))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG_DEFAULT, "password");
        }
        public UsersCommand.UsersLogin toCommand() {
            return UsersCommand.UsersLogin.nonState(email, BCryptUtils.hash(password));
        }
    }
}
