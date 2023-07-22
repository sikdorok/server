package com.ddd.chulsi.domainCore.model.feed;

import java.util.UUID;

public interface FeedService {

    Feed register(Feed insertFeed);

    Feed findByFeedId(UUID feedId);

    void updateMain(UUID usersId, boolean isMain);

    void delete(Feed feed);

}
