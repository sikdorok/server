package com.sikdorok.appapi.infrastructure.specification.users;

import com.sikdorok.domaincore.model.users.Users;
import com.sikdorok.domaincore.model.users.UsersJpaRepository;
import com.sikdorok.appapi.infrastructure.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsersSpecificationImpl implements UsersSpecification {

    private final UsersJpaRepository usersJpaRepository;

    @Override
    public Users findByUsersId(UUID usersId) {
        return usersJpaRepository.findByUsersId(usersId).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Users findByEmail(String email) {
        return usersJpaRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

}
