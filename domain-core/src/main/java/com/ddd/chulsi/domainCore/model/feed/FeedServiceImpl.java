package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedReader feedReader;
    private final FeedStore feedStore;

    @Override
    public Feed register(Feed insertFeed) {
        return feedStore.register(insertFeed);
    }

    @Override
    public Feed findByFeedId(UUID feedId) {
        return feedReader.findByFeedId(feedId);
    }

    @Override
    public void updateMain(UUID usersId, boolean isMain, LocalDateTime time) {
        Feed mainFeed = feedReader.findByUsersIdAndIsMainAndTime(usersId, isMain, time);
        if (mainFeed != null) mainFeed.updateIsMainFalse();
    }

    @Override
    public void delete(Feed feed) {
        feedStore.delete(feed);
    }

    @Override
    public List<FeedInfo.WeeklyCover> weeklyList(UUID usersId, LocalDate date) {
        List<FeedInfo.Weekly> weeklyInfo = feedReader.weeklyList(usersId, date);

        List<FeedInfo.WeeklyFeed> firstWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 1).map(FeedInfo.WeeklyFeed::toDTO).toList());
        if (firstWeek.size() != 7) addPrevWeekly(usersId, date, firstWeek);

        List<FeedInfo.WeeklyFeed> secondWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 2).map(FeedInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.WeeklyFeed> thirdWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 3).map(FeedInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.WeeklyFeed> fourthWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 4).map(FeedInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.WeeklyFeed> fifthWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 5).map(FeedInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.WeeklyFeed> sixthWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 6).map(FeedInfo.WeeklyFeed::toDTO).toList());

        if (!sixthWeek.isEmpty() && sixthWeek.size() != 7) addNextWeekly(usersId, date, sixthWeek);
        else if (fifthWeek.size() != 7) addNextWeekly(usersId, date, fifthWeek);

        return Arrays.asList(
            new FeedInfo.WeeklyCover(1, firstWeek),
            new FeedInfo.WeeklyCover(2, secondWeek),
            new FeedInfo.WeeklyCover(3, thirdWeek),
            new FeedInfo.WeeklyCover(4, fourthWeek),
            new FeedInfo.WeeklyCover(5, fifthWeek),
            new FeedInfo.WeeklyCover(6, sixthWeek)
        );
    }

    @Override
    public Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand, DefinedCode initTag) {
        return feedReader.findAllByUsersIdAndTime(usersId, listCommand, initTag);
    }

    @Override
    public List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate) {
        return feedReader.listViewWithCursorBased(usersId, listViewCommand, nextCursorDate);
    }

    @Override
    public String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand) {
        return feedReader.nextCursorDate(usersId, listViewCommand);
    }

    @Override
    public List<FeedInfo.FeedSimpleInfo> findAllByUsersId(UUID usersId) {
        return feedReader.findAllByUsersId(usersId);
    }

    @Override
    public void revokeUsers(UUID usersId) {
        feedStore.revokeUsers(usersId);
    }

    @Override
    public List<DefinedCode> getOnlyTags(UUID usersId, LocalDate date) {
        return feedReader.getOnlyTags(usersId, date);
    }

    private void addPrevWeekly(UUID usersId, LocalDate date, List<FeedInfo.WeeklyFeed> originWeek) {
        int gap = 7 - originWeek.size();
        List<FeedInfo.Weekly> prevWeeklyInfo = feedReader.weeklyList(usersId, date.minusMonths(1));
        List<FeedInfo.Weekly> subList = prevWeeklyInfo.subList(prevWeeklyInfo.size() - gap, prevWeeklyInfo.size());
        List<FeedInfo.WeeklyFeed> subListConvert = new ArrayList<>(subList.stream().map(FeedInfo.WeeklyFeed::toDTO).toList());
        originWeek.addAll(0, subListConvert);
    }

    private void addNextWeekly(UUID usersId, LocalDate date, List<FeedInfo.WeeklyFeed> originWeek) {
        int gap = 7 - originWeek.size();
        List<FeedInfo.Weekly> nextWeeklyInfo = feedReader.weeklyList(usersId, date.plusMonths(1));
        List<FeedInfo.Weekly> subList = nextWeeklyInfo.subList(0, gap);
        List<FeedInfo.WeeklyFeed> subListConvert = new ArrayList<>(subList.stream().map(FeedInfo.WeeklyFeed::toDTO).toList());
        originWeek.addAll(originWeek.size(), subListConvert);
    }

}
