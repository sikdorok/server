package com.ddd.chulsi.domainCore.model.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public void updateMain(UUID usersId, boolean isMain) {
        Feed mainFeed = feedReader.findByUsersIdAndIsMain(usersId, isMain);
        if (mainFeed != null) mainFeed.updateIsMainFalse();
    }

    @Override
    public void delete(Feed feed) {
        feedStore.delete(feed);
    }

    @Override
    public List<List<FeedInfo.HomeInfo.WeeklyFeed>> weeklyList(UUID usersId, LocalDate date) {
        List<FeedInfo.HomeInfo.Weekly> weeklyInfo = feedReader.weeklyList(usersId, date);

        List<FeedInfo.HomeInfo.WeeklyFeed> firstWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 1).map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        if (firstWeek.size() != 7) addPrevWeekly(usersId, date, firstWeek);

        List<FeedInfo.HomeInfo.WeeklyFeed> secondWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 2).map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.HomeInfo.WeeklyFeed> thirdWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 3).map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.HomeInfo.WeeklyFeed> fourthWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 4).map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.HomeInfo.WeeklyFeed> fifthWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 5).map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        List<FeedInfo.HomeInfo.WeeklyFeed> sixthWeek = new ArrayList<>(weeklyInfo.stream().filter(weekly -> weekly.getWeek() == 6).map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());

        if (!sixthWeek.isEmpty() && sixthWeek.size() != 7) addNextWeekly(usersId, date, sixthWeek);
        else if (fifthWeek.size() != 7) addNextWeekly(usersId, date, fifthWeek);

        return Arrays.asList(firstWeek, secondWeek, thirdWeek, fourthWeek, fifthWeek, sixthWeek);
    }

    @Override
    public Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.HomeCommand homeCommand) {
        return feedReader.findAllByUsersIdAndTime(usersId, homeCommand);
    }

    private void addPrevWeekly(UUID usersId, LocalDate date, List<FeedInfo.HomeInfo.WeeklyFeed> originWeek) {
        int gap = 7 - originWeek.size();
        List<FeedInfo.HomeInfo.Weekly> prevWeeklyInfo = feedReader.weeklyList(usersId, date.minusMonths(1));
        List<FeedInfo.HomeInfo.Weekly> subList = prevWeeklyInfo.subList(prevWeeklyInfo.size() - gap, prevWeeklyInfo.size());
        List<FeedInfo.HomeInfo.WeeklyFeed> subListConvert = new ArrayList<>(subList.stream().map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        originWeek.addAll(0, subListConvert);
    }

    private void addNextWeekly(UUID usersId, LocalDate date, List<FeedInfo.HomeInfo.WeeklyFeed> originWeek) {
        int gap = 7 - originWeek.size();
        List<FeedInfo.HomeInfo.Weekly> nextWeeklyInfo = feedReader.weeklyList(usersId, date.plusMonths(1));
        List<FeedInfo.HomeInfo.Weekly> subList = nextWeeklyInfo.subList(0, gap);
        List<FeedInfo.HomeInfo.WeeklyFeed> subListConvert = new ArrayList<>(subList.stream().map(FeedInfo.HomeInfo.WeeklyFeed::toDTO).toList());
        originWeek.addAll(originWeek.size(), subListConvert);
    }

}
