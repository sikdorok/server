package com.sikdorok.domaincore.model.feed;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FeedReader {

    Feed findByFeedId(UUID feedId);

    List<FeedInfo.Weekly> weeklyList(UUID usersId, LocalDate date);

    Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand, DefinedCode initTag);

    List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate);

    String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand);

    List<FeedInfo.FeedSimpleInfo> findAllByUsersId(UUID usersId);

    List<DefinedCode> getOnlyTags(UUID usersId, LocalDate date);

    boolean duplicateCheck(LocalDateTime time, DefinedCode tag, DefinedCode icon, String memo);

}
