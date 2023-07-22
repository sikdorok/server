package com.ddd.chulsi.domainCore.model.feed;

public interface FeedStore {

    Feed register(Feed insertFeed);

    void delete(Feed feed);

}
