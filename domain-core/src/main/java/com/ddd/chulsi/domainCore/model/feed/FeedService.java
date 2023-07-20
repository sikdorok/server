package com.ddd.chulsi.domainCore.model.feed;

import java.util.UUID;

public interface FeedService {

    void register(Feed insertFeed);

    Feed findByFeedId(UUID feedId);

}
