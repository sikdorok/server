package com.ddd.chulsi.domainCore.model.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedStoreImpl implements FeedStore {

    private final FeedJpaRepository feedJpaRepository;

    @Override
    public Feed register(Feed insertFeed) {
        return feedJpaRepository.save(insertFeed);
    }

    @Override
    public void delete(Feed feed) {
        feedJpaRepository.delete(feed);
    }

    @Override
    public void revokeUsers(UUID usersId) {
        feedJpaRepository.revokeUsers(usersId);
    }

    @Override
    public void store(Feed mainFeed) {
        feedJpaRepository.save(mainFeed);
    }

    @Override
    public void flush() {
        feedJpaRepository.flush();
    }

}
