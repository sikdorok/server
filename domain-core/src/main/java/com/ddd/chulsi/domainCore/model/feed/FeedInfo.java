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
        String date,
        String time,
        String memo,
        DefinedCode icon,
        boolean isMain
    ) {
        public static FeedInfoDTO nonState(UUID feedId, boolean isMine, DefinedCode tag, LocalDateTime timeFull, String memo, DefinedCode icon, boolean isMain) {
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일", Locale.KOREAN);
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREAN);

            String date = timeFull.format(dateFormat);
            String time = timeFull.format(timeFormat);
            return new FeedInfoDTO(feedId, isMine, tag, date, time, memo, icon, isMain);
        }
    }

}
