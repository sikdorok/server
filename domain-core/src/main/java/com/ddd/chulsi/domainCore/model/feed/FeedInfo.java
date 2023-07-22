package com.ddd.chulsi.domainCore.model.feed;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public class FeedInfo {

    public record FeedInfoDTO (
        UUID feedId,
        boolean isMine,
        DefinedCode tag,
        LocalDateTime time,
        String memo,
        DefinedCode icon,
        boolean isMain
    ) {
        public static FeedInfoDTO toDTO(Feed feed, UUID usersId) {
            return new FeedInfoDTO(
                feed.getFeedId(),
                usersId.equals(feed.getUsersId()),
                DefinedCode.valueOf(feed.getTag()),
                feed.getTime(),
                feed.getMemo(),
                feed.getIcon(),
                feed.isMain()
            );
        }
    }

}
