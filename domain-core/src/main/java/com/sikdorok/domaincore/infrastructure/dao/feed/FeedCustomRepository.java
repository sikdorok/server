package com.sikdorok.domaincore.infrastructure.dao.feed;

import com.sikdorok.domaincore.model.feed.FeedCommand;
import com.sikdorok.domaincore.model.feed.FeedInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedCustomRepository {

    Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand, DefinedCode initTag);

    List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate);

    String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand);

    List<FeedInfo.FeedSimpleInfo> findAllByUsersId(UUID usersId);

    List<DefinedCode> getOnlyTags(UUID usersId, LocalDate date);

}
