package com.ddd.chulsi.domainCore.model.users;

import java.util.UUID;

public class UsersCommand {

    public record UsersLogin(String email, String password) { }

    public record UsersRefreshTokenUpdateEvent(UUID usersId, String refreshToken) { }

}
