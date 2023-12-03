package com.ddd.chulsi.domainCore.model.feed;

import java.util.UUID;

public interface FeedStore {

    Feed register(Feed insertFeed);

    void delete(Feed feed);

    void revokeUsers(UUID usersId);

    void store(Feed mainFeed);

    void flush();

}
