package com.ddd.chulsi.domainCore.model.users;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

        @JsonIgnore
        private final Long oauthId;

        @JsonIgnore
        private final String oauthAccessToken;

        @JsonIgnore
        private final String refreshToken;

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
