package com.sikdorok.domaincore.model.users;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.UUID;

public interface UsersService {

    Users findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

    Users store(Users users);

    Users findByUsersId(UUID usersId);

    Users findByEmailAndPassword(String email, String password);

    boolean duplicationCheckEmail(String email);

    boolean duplicationCheckEmailNonLock(String email);

    Users findByEmail(String email);

    void revoke(Users users);

}
