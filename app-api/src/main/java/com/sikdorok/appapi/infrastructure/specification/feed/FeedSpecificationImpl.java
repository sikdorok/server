package com.sikdorok.appapi.infrastructure.specification.feed;

import com.sikdorok.domaincore.model.feed.Feed;
import com.sikdorok.domaincore.model.feed.FeedJpaRepository;
import com.sikdorok.appapi.infrastructure.exception.BadRequestException;
import com.sikdorok.appapi.infrastructure.exception.NotFoundException;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FeedSpecificationImpl implements FeedSpecification {

    private final FeedJpaRepository feedJpaRepository;

    @Override
    public Feed findByFeedId(UUID feedId) {
        return feedJpaRepository.findByFeedId(feedId).orElseThrow(NotFoundException::new);
    }

    @Override
    public Feed findByFeedIdAndIsMind(UUID feedId, UUID usersId) {
        Feed feed = feedJpaRepository.findByFeedId(feedId).orElse(null);
        if (feed == null) throw new NotFoundException();

        if (!feed.getUsersId().equals(usersId))
            throw new BadRequestException(ErrorMessage.FORBIDDEN);

        return feed;
    }

}
