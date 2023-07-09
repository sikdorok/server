package com.ddd.chulsi.domainCore.model.users;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersJpaRepository extends JpaRepository<Users, UUID> {

    Optional<Users> findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

    Optional<Users> findByUsersId(UUID usersId);

    Optional<Users> findByEmailAndPassword(String email, String password);

    Optional<Users> findFirstByEmail(String email);

}
