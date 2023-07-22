package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FeedInfo {

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
        public static FeedInfoDTO toDTO(Feed feed, UUID usersId, List<PhotosInfo.Info> photosInfoList, int photosLimit) {
            return new FeedInfoDTO(
                feed.getFeedId(),
                usersId.equals(feed.getUsersId()),
                DefinedCode.valueOf(feed.getTag()),
                feed.getTime(),
                feed.getMemo(),
                feed.getIcon(),
                feed.isMain(),
                photosInfoList,
                photosLimit
            );
        }
    }

}
