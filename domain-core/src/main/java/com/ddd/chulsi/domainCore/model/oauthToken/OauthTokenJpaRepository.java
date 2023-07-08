package com.ddd.chulsi.domainCore.model.oauthToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OauthTokenJpaRepository extends JpaRepository<OauthToken, UUID> {
}
