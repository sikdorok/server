package com.ddd.chulsi.domainCore.model.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedReader feedReader;
    private final FeedStore feedStore;

    @Override
    public void register(Feed insertFeed) {
        feedStore.register(insertFeed);
    }

    @Override
    public Feed findByFeedId(UUID feedId) {
        return feedReader.findByFeedId(feedId);
    }

}
