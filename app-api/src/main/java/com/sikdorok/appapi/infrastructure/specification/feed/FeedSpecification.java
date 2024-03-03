package com.sikdorok.appapi.infrastructure.specification.feed;

import com.sikdorok.domaincore.model.feed.Feed;

import java.util.UUID;

public interface FeedSpecification {

    Feed findByFeedId(UUID feedId);

    Feed findByFeedIdAndIsMind(UUID feedId, UUID usersId);

}
