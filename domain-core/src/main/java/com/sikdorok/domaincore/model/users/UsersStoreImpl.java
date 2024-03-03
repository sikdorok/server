package com.sikdorok.domaincore.model.users;

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

    @Override
    public void delete(Users users) {
        usersJpaRepository.delete(users);
    }
}
