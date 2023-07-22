package com.ddd.chulsi.infrastructure.specification.users;

import com.ddd.chulsi.domainCore.model.users.Users;

import java.util.UUID;

public interface UsersSpecification {

    Users findByUsersId(UUID usersId);

}
