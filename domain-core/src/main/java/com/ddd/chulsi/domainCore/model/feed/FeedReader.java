package com.ddd.chulsi.domainCore.model.feed;

import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedReader {

    Feed findByFeedId(UUID feedId);

    Feed findByUsersIdAndIsMain(UUID usersId, boolean isMain);

    List<FeedInfo.HomeInfo.Weekly> weeklyList(UUID usersId, LocalDate date);

    Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.HomeCommand homeCommand);

}
