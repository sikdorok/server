package com.sikdorok.appapi.application.admin;

import com.sikdorok.domaincore.model.users.Users;
import com.sikdorok.appapi.infrastructure.specification.users.UsersSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminFacade {

    private final UsersSpecification usersSpecification;

    @Value("${init-item.password}")
    private String initPassword;

    @Transactional(rollbackFor = Exception.class)
    public void passwordReset(UUID usersId) {
        Users users = usersSpecification.findByUsersId(usersId);
        users.passwordReset(initPassword);
    }

    @Transactional(readOnly = true)
    public UUID findIdByEmail(String email) {
        Users users = usersSpecification.findByEmail(email);
        return users.getUsersId();
    }
}
