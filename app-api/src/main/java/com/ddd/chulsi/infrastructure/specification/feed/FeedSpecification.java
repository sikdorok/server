package com.ddd.chulsi.infrastructure.specification.feed;

import com.ddd.chulsi.domainCore.model.feed.Feed;

import java.util.UUID;

public interface FeedSpecification {

    Feed findByFeedId(UUID feedId);

    Feed findByFeedIdAndIsMind(UUID feedId, UUID usersId);

}
