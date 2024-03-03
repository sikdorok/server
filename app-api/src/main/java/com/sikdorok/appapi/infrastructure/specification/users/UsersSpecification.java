package com.sikdorok.appapi.infrastructure.specification.users;

import com.sikdorok.domaincore.model.users.Users;

import java.util.UUID;

public interface UsersSpecification {

    Users findByUsersId(UUID usersId);

    Users findByEmail(String email);

}
