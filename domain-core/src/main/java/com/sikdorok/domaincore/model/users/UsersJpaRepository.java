package com.sikdorok.domaincore.model.users;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersJpaRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

    Optional<Users> findByUsersId(UUID usersId);

    Optional<Users> findByEmailAndPassword(String email, String password);

    Optional<Users> findFirstByEmail(String email);

    Optional<Users> findByEmail(String email);

}
