package com.ddd.chulsi.domainCore.infrastructure.dao.feed;

import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface FeedCustomRepository {

    Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand);

    List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate);

    String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand);

}
