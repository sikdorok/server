package com.ddd.chulsi.infrastructure.inMemory.feed;

import com.ddd.chulsi.domainCore.model.feed.Feed;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.util.DateUtil;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.PagingDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ddd.chulsi.infrastructure.inMemory.photos.PhotosFactory.givenPhotosInfoList;

public class FeedFactory {

    public static Feed givenFeed() {
        return Feed.builder()
            .feedId(UUID.randomUUID())
            .usersId(UUID.randomUUID())
            .tag(DefinedCode.C000300001.toString())
            .time(LocalDateTime.now())
            .memo("메모")
            .icon(DefinedCode.C000400001)
            .isMain(false)
            .build();
    }

    public static FeedDTO.FeedRegisterRequest givenRegisterRequest() {
        return new FeedDTO.FeedRegisterRequest(
            DefinedCode.C000300001,
            LocalDateTime.now(),
            "메모",
            DefinedCode.C000400001,
            false
        );
    }

    public static FeedDTO.FeedInfoUpdateRequest givenInfoUpdateRequest() {
        return new FeedDTO.FeedInfoUpdateRequest(
            UUID.randomUUID(),
            DefinedCode.C000300001,
            LocalDateTime.now(),
            "메모",
            DefinedCode.C000400001,
            false,
            Set.of(UUID.randomUUID(), UUID.randomUUID())
        );
    }

    public static FeedInfo.FeedInfoDTO givenFeedInfo() {
        return FeedInfo.FeedInfoDTO.toDTO(
            givenFeed(),
            true,
            givenPhotosInfoList(),
            0
        );
    }

    public static FeedDTO.FeedInfoResponse givenFeedInfoResponse() {
        return new FeedDTO.FeedInfoResponse("식도록", givenFeedInfo());
    }

    public static List<FeedInfo.WeeklyFeed> givenWeeklyFeed(LocalDate yearMonth, int week) {
        List<LocalDate> weekDays = DateUtil.getWeekDays(yearMonth, week);
        return weekDays.stream().map(day -> new FeedInfo.WeeklyFeed(week, day, DefinedCode.C000400001)).collect(Collectors.toList());
    }

    public static FeedDTO.MonthlyResponse givenMonthlyResponse() {
        return new FeedDTO.MonthlyResponse(
            LocalDate.now(),
            Arrays.asList(
                new FeedInfo.WeeklyCover(1, givenWeeklyFeed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1), 1)),
                new FeedInfo.WeeklyCover(2, givenWeeklyFeed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1), 2)),
                new FeedInfo.WeeklyCover(3, givenWeeklyFeed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1), 3)),
                new FeedInfo.WeeklyCover(4, givenWeeklyFeed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1), 4)),
                new FeedInfo.WeeklyCover(5, givenWeeklyFeed(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1), 5))
            )
        );
    }

    public static FeedInfo.HomeFeedItem givenHomeFeedItem() {
        return new FeedInfo.HomeFeedItem(
            UUID.randomUUID(),
            DefinedCode.C000400001,
            false,
            DefinedCode.C000300001,
            "오후 12:04",
            "메모",
            givenPhotosInfoList()
        );
    }

    public static FeedDTO.ListResponse givenListResponse() {
        return new FeedDTO.ListResponse(
            new PagingDTO(1, 1, 2),
            List.of(givenHomeFeedItem(), givenHomeFeedItem())
        );
    }

}
