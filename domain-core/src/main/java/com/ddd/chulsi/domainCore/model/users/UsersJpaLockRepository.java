package com.ddd.chulsi.domainCore.model.users;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;
import java.util.UUID;

public interface UsersJpaLockRepository extends JpaRepository<Users, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Users> findFirstByEmail(String email);

}
