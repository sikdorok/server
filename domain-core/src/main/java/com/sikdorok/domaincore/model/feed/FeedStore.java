package com.sikdorok.domaincore.model.feed;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FeedStore {

    Feed register(Feed insertFeed);

    void delete(Feed feed);

    void revokeUsers(UUID usersId);

    void store(Feed mainFeed);

    void flush();

    void allDisableIsMain(UUID usersId, LocalDateTime time);

}
