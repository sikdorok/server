package com.ddd.chulsi.infrastructure.inMemory.feed;

import com.ddd.chulsi.domainCore.model.feed.Feed;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;

import java.time.LocalDateTime;
import java.util.UUID;

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
            UUID.randomUUID()
        );
    }

    public static FeedInfo.FeedInfoDTO givenFeedInfo() {
        return FeedInfo.FeedInfoDTO.toDTO(
            givenFeed(),
            UUID.randomUUID()
        );
    }

    public static FeedDTO.FeedInfoResponse givenFeedInfoResponse() {
        return new FeedDTO.FeedInfoResponse(givenFeedInfo());
    }

}
