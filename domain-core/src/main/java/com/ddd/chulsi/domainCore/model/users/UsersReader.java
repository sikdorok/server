package com.ddd.chulsi.domainCore.model.users;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.util.UUID;

public interface UsersReader {

    Users findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

    Users findByUsersId(UUID usersId);

    Users findByEmailAndPassword(String email, String password);

    boolean duplicationCheckEmail(String email);

    Users findByEmail(String email);

}
