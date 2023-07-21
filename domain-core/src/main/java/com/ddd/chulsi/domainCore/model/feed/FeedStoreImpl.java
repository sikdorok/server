package com.ddd.chulsi.domainCore.model.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedStoreImpl implements FeedStore {

    private final FeedJpaRepository feedJpaRepository;

    @Override
    public void register(Feed insertFeed) {
        feedJpaRepository.save(insertFeed);
    }

    @Override
    public void delete(Feed feed) {
        feedJpaRepository.delete(feed);
    }

}
