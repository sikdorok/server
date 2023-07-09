package com.ddd.chulsi.domainCore.model.oauthToken;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OauthTokenJpaRepository extends JpaRepository<OauthToken, UUID> {

    Optional<OauthToken> findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

}
