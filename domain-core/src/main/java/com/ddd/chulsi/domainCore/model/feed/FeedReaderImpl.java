package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.infrastructure.dao.feed.FeedCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public Feed findByUsersIdAndIsMain(UUID usersId, boolean isMain) {
        return feedJpaRepository.findByUsersIdAndIsMain(usersId, isMain).orElse(null);
    }

    @Override
    public List<FeedInfo.Weekly> weeklyList(UUID usersId, LocalDate date) {
        YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return feedJpaRepository.weeklyList(usersId, startDate, endDate);
    }

    @Override
    public Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand) {
        return feedCustomRepository.findAllByUsersIdAndTime(usersId, listCommand);
    }
}
