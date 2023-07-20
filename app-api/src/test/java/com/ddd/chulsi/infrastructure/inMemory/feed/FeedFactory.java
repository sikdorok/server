package com.ddd.chulsi.infrastructure.inMemory.feed;

import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;

import java.time.LocalDateTime;
import java.util.UUID;

public class FeedFactory {

    public static FeedDTO.FeedRegisterRequest givenRegisterRequest() {
        return new FeedDTO.FeedRegisterRequest(
            DefinedCode.C000300001,
            LocalDateTime.now(),
            "메모",
            DefinedCode.C000400001,
            false
        );
    }

    public static FeedInfo.FeedInfoDTO givenFeedInfo() {
        return FeedInfo.FeedInfoDTO.nonState(
            UUID.randomUUID(),
            true,
            DefinedCode.C000300001,
            LocalDateTime.now(),
            "메모",
            DefinedCode.C000400001,
            false
        );
    }

    public static FeedDTO.FeedInfoResponse givenFeedInfoResponse() {
        return new FeedDTO.FeedInfoResponse(givenFeedInfo());
    }

}
