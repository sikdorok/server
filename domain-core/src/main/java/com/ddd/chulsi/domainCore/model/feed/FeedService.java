package com.ddd.chulsi.domainCore.model.feed;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedService {

    Feed register(Feed insertFeed);

    Feed findByFeedId(UUID feedId);

    void updateMain(UUID usersId, boolean isMain);

    void delete(Feed feed);

    List<List<FeedInfo.HomeInfo.WeeklyFeed>> weeklyList(UUID usersId, LocalDate date);

    Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.HomeCommand homeCommand);

}
