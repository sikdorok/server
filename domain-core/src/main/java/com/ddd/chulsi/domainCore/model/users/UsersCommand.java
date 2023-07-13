package com.ddd.chulsi.domainCore.model.users;

import java.util.UUID;

public class UsersCommand {

    public record LoginCommand(
        String authorizationCode,
        String state
    ) {
        public static LoginCommand nonState(String authorizationCode) {
            return new LoginCommand(authorizationCode, null);
        }
    }

    public record UsersLogin(
        String email,
        String password
    ) {
        public static UsersLogin nonState(String email, String password) {
            return new UsersLogin(email, password);
        }
    }

    public record UsersRefreshTokenUpdateEvent(
        UUID usersId,
        String refreshToken
    ) { }

    public record RegisterCommand(
        String nickname,
        String email,
        String password
    ) {
        public static RegisterCommand nonState(String nickname, String email, String password) {
            return new RegisterCommand(nickname, email, password);
        }
        public Users toEntity() {
            return Users.builder()
                .name(nickname)
                .email(email)
                .password(password)
                .build();
        }
    }

    public record PasswordFind(
        String email
    ) {
        public static PasswordFind nonState(String email) {
            return new PasswordFind(email);
        }
    }
}
