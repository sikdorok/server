package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FeedReader {

    Feed findByFeedId(UUID feedId);

    Feed findByUsersIdAndIsMain(UUID usersId, boolean isMain);

    List<FeedInfo.Weekly> weeklyList(UUID usersId, LocalDate date);

    Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand, DefinedCode initTag);

    List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate);

    String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand);

    List<FeedInfo.FeedSimpleInfo> findAllByUsersId(UUID usersId);

    List<DefinedCode> getOnlyTags(UUID usersId, LocalDate date);

}
