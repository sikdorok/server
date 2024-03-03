package com.sikdorok.domaincore.model.users;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final UsersReader usersReader;
    private final UsersStore usersStore;

    @Override
    public Users findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId) {
        return usersReader.findByOauthTypeAndOauthId(oauthType, oauthId);
    }

    @Override
    public Users store(Users users) {
        return usersStore.save(users);
    }

    @Override
    public Users findByUsersId(UUID usersId) {
        return usersReader.findByUsersId(usersId);
    }

    @Override
    public Users findByEmailAndPassword(String email, String password) {
        return usersReader.findByEmailAndPassword(email, password);
    }

    @Override
    public boolean duplicationCheckEmail(String email) {
        return usersReader.duplicationCheckEmail(email);
    }

    @Override
    public boolean duplicationCheckEmailNonLock(String email) {
        return usersReader.duplicationCheckEmailNonLock(email);
    }

    @Override
    public Users findByEmail(String email) {
        return usersReader.findByEmail(email);
    }

    @Override
    public void revoke(Users users) {
        usersStore.delete(users);
    }

}
