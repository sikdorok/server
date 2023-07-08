package com.ddd.chulsi.domainCore.model.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersStoreImpl implements UsersStore {

    private final UsersJpaRepository usersJpaRepository;

    @Override
    public Users save(Users users) {
        return usersJpaRepository.save(users);
    }
}
