package com.sikdorok.domaincore.model.feed;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FeedInfo {

    public record FeedSimpleInfo (
        UUID feedId,
        List<PhotosInfo.Info> photosInfoList
    ) { }

    public record FeedInfoDTO (
        UUID feedId,
        boolean isMine,
        DefinedCode tag,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime time,
        String memo,
        DefinedCode icon,
        boolean isMain,
        List<PhotosInfo.Info> photosInfoList,
        int photosLimit
    ) {
        public static FeedInfoDTO toDTO(Feed feed, boolean isMine, List<PhotosInfo.Info> photosInfoList, int photosLimit) {
            return new FeedInfoDTO(
                feed.getFeedId(),
                isMine,
                feed.getTag(),
                feed.getTime(),
                feed.getMemo(),
                feed.getIcon(),
                feed.isMain(),
                photosInfoList,
                photosLimit
            );
        }
    }

    public record HomeFeedItemDTO(
        UUID feedId,
        DefinedCode icon,
        boolean isMain,
        DefinedCode tag,
        LocalDateTime time,
        String memo,
        List<PhotosInfo.Info> photosInfoList
    ) {
    }

    public record HomeFeedItem(
        UUID feedId,
        DefinedCode icon,
        boolean isMain,

        DefinedCode tag,
        String time,
        String memo,
        List<PhotosInfo.Info> photosInfoList
    ) {
    }

    public record WeeklyCover(
        int week,
        List<WeeklyFeed> weeklyFeeds
    ) {

    }

    public record WeeklyFeed(
        @JsonIgnore
        int week,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        LocalDate time,
        DefinedCode icon
    ) {
        public static WeeklyFeed toDTO(FeedInfo.Weekly weekly) {
            return new WeeklyFeed(weekly.getWeek(), weekly.getTime(), weekly.getIcon());
        }
    }

    public interface Weekly {
        int getWeek();
        LocalDate getTime();
        DefinedCode getIcon();
    }

    public record HomeListViewFeedItemDTO(
        UUID feedId,
        DefinedCode icon,
        boolean isMain,
        DefinedCode tag,
        LocalDateTime time,
        String memo,
        List<PhotosInfo.Info> photosInfoList
    ) {
    }

    public record HomeListViewFeedItem(
        UUID feedId,
        DefinedCode icon,
        boolean isMain,

        @JsonIgnore
        DefinedCode tag,

        @JsonIgnore
        String date,

        @JsonIgnore
        LocalDateTime originTime,

        String time,
        String memo,
        List<PhotosInfo.Info> photosInfoList
    ) {
    }

    public record HomeListViewDTO(
        String date,
        Map<String, List<HomeListViewFeedItem>> feeds
    ) {

    }

}
