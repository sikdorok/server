package com.sikdorok.domaincore.model.users;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsersInfo {

    @Getter
    public static class UsersInfoLogin {
        private final UUID usersId;

        @JsonIgnore
        private final DefinedCode auth;

        @Setter
        private String accessToken;

        @Setter
        private String refreshToken;

        @JsonIgnore
        private final Long oauthId;

        @JsonIgnore
        private final String oauthAccessToken;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private final LocalDateTime lastLoginAt;

        public UsersInfoLogin(Users users, String oauthAccessToken) {
            this.usersId = users.getUsersId();
            this.auth = users.getAuth();
            this.oauthId = users.getOauthId();
            this.oauthAccessToken = oauthAccessToken;
            this.refreshToken = users.getRefreshToken();
            this.lastLoginAt = users.getLastLoginAt();
        }
    }

}
