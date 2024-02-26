package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.infrastructure.dao.feed.FeedCustomRepository;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedReaderImpl implements FeedReader {

    private final FeedJpaRepository feedJpaRepository;
    private final FeedCustomRepository feedCustomRepository;

    @Override
    public Feed findByFeedId(UUID feedId) {
        return feedJpaRepository.findByFeedId(feedId).orElse(null);
    }

    @Override
    public List<FeedInfo.Weekly> weeklyList(UUID usersId, LocalDate date) {
        YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return feedJpaRepository.weeklyList(usersId, startDate, endDate);
    }

    @Override
    public Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand, DefinedCode initTag) {
        return feedCustomRepository.findAllByUsersIdAndTime(usersId, listCommand, initTag);
    }

    @Override
    public List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate) {
        return feedCustomRepository.listViewWithCursorBased(usersId, listViewCommand, nextCursorDate);
    }

    @Override
    public String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand) {
        return feedCustomRepository.nextCursorDate(usersId, listViewCommand);
    }

    @Override
    public List<FeedInfo.FeedSimpleInfo> findAllByUsersId(UUID usersId) {
        return feedCustomRepository.findAllByUsersId(usersId);
    }

    @Override
    public List<DefinedCode> getOnlyTags(UUID usersId, LocalDate date) {
        return feedCustomRepository.getOnlyTags(usersId, date);
    }

    @Override
    public boolean duplicateCheck(LocalDateTime time, DefinedCode tag, DefinedCode icon, String memo) {
        return feedJpaRepository.findByTimeAndTagAndIconAndMemo(time, tag, icon, memo).isPresent();
    }

}
