package com.ddd.chulsi.domainCore.model.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedReaderImpl implements FeedReader {

    private final FeedJpaRepository feedJpaRepository;

    @Override
    public Feed findByFeedId(UUID feedId) {
        return feedJpaRepository.findByFeedId(feedId).orElse(null);
    }

    @Override
    public Feed findByUsersIdAndIsMain(UUID usersId, boolean isMain) {
        return feedJpaRepository.findByUsersIdAndIsMain(usersId, isMain).orElse(null);
    }
}
