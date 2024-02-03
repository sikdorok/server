package com.ddd.chulsi.domainCore.model.users;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersReaderImpl implements UsersReader {

    private final UsersJpaRepository usersJpaRepository;
    private final UsersJpaLockRepository usersJpaLockRepository;

    @Override
    public Users findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId) {
        return usersJpaRepository.findByOauthTypeAndOauthId(oauthType, oauthId).orElse(null);
    }

    @Override
    public Users findByUsersId(UUID usersId) {
        return usersJpaRepository.findByUsersId(usersId).orElse(null);
    }

    @Override
    public Users findByEmailAndPassword(String email, String password) {
        return usersJpaRepository.findByEmailAndPassword(email, password).orElse(null);
    }

    @Override
    public boolean duplicationCheckEmail(String email) {
        return usersJpaLockRepository.findFirstByEmail(email).isPresent();
    }

    @Override
    public Users findByEmail(String email) {
        return usersJpaRepository.findByEmail(email).orElse(null);
    }

    @Override
    public boolean duplicationCheckEmailNonLock(String email) {
        return usersJpaRepository.findFirstByEmail(email).isPresent();
    }

}
