package com.ddd.chulsi.application.feed;

import com.ddd.chulsi.domainCore.model.feed.Feed;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.feed.FeedService;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.exception.NotFoundException;
import com.ddd.chulsi.infrastructure.jwt.JWTClaim;
import com.ddd.chulsi.infrastructure.jwt.JWTProperties;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    private final FeedService feedService;

    @Transactional(rollbackOn = Exception.class)
    public void register(String token, FeedCommand.RegisterCommand registerCommand, MultipartFile file) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID userId = jwtClaim.getUsersId();

        Feed insertFeed = registerCommand.toEntity(userId);
        feedService.register(insertFeed);
        
        // TODO - 사진등록 photos 도메인 할 때 처리
    }

    public FeedDTO.FeedInfoResponse info(String token, UUID feedId) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        Feed feed = feedService.findByFeedId(feedId);
        if (feed == null) throw new NotFoundException();

        // TODO - photos 도메인 할 때 사진정보 Response 추가
        FeedInfo.FeedInfoDTO feedInfoDTO = FeedInfo.FeedInfoDTO.nonState(
            feed.getFeedId(),
            usersId.equals(feed.getUsersId()),
            DefinedCode.valueOf(feed.getTag()),
            feed.getTime(),
            feed.getMemo(),
            feed.getIcon(),
            feed.isMain()
        );

        return new FeedDTO.FeedInfoResponse(
            feedInfoDTO
        );
    }
}
