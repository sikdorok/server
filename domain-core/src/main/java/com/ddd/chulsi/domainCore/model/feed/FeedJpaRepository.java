package com.ddd.chulsi.domainCore.model.feed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FeedJpaRepository extends JpaRepository<Feed, UUID> {

    Optional<Feed> findByFeedId(UUID feedId);

    Optional<Feed> findByUsersIdAndIsMain(UUID usersId, boolean isMain);

}
