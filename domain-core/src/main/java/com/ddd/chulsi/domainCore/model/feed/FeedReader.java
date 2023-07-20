package com.ddd.chulsi.domainCore.model.feed;

import java.util.UUID;

public interface FeedReader {

    Feed findByFeedId(UUID feedId);

}
