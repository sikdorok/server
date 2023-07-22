package com.ddd.chulsi.infrastructure.specification.users;

import com.ddd.chulsi.domainCore.model.users.Users;
import com.ddd.chulsi.domainCore.model.users.UsersJpaRepository;
import com.ddd.chulsi.infrastructure.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsersSpecificationImpl implements UsersSpecification {

    private final UsersJpaRepository usersJpaRepository;

    @Override
    public Users findByUsersId(UUID usersId) {
        return usersJpaRepository.findByUsersId(usersId).orElseThrow(UserNotFoundException::new);
    }

}
