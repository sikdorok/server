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
    public Feed register(Feed insertFeed) {
        return feedStore.register(insertFeed);
    }

    @Override
    public Feed findByFeedId(UUID feedId) {
        return feedReader.findByFeedId(feedId);
    }

    @Override
    public Feed findByUsersIdAndIsMain(UUID usersId, boolean isMain) {
        return feedReader.findByUsersIdAndIsMain(usersId, isMain);
    }

    @Override
    public void delete(Feed feed) {
        feedStore.delete(feed);
    }

}
