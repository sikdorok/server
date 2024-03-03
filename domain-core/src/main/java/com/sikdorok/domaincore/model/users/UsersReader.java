package com.sikdorok.domaincore.model.users;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.UUID;

public interface UsersReader {

    Users findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

    Users findByUsersId(UUID usersId);

    Users findByEmailAndPassword(String email, String password);

    boolean duplicationCheckEmail(String email);

    Users findByEmail(String email);

    boolean duplicationCheckEmailNonLock(String email);

}
