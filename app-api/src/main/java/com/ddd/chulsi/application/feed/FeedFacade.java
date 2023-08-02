package com.ddd.chulsi.application.feed;

import com.ddd.chulsi.domainCore.model.feed.Feed;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.feed.FeedService;
import com.ddd.chulsi.domainCore.model.photos.Photos;
import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.photos.PhotosService;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.domainCore.model.users.Users;
import com.ddd.chulsi.infrastructure.aws.FileProvider;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
import com.ddd.chulsi.infrastructure.jwt.JWTClaim;
import com.ddd.chulsi.infrastructure.jwt.JWTProperties;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.infrastructure.mapper.feed.FeedMapper;
import com.ddd.chulsi.infrastructure.specification.feed.FeedSpecification;
import com.ddd.chulsi.infrastructure.specification.users.UsersSpecification;
import com.ddd.chulsi.infrastructure.util.CollectionUtils;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.PagingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    private final FileProvider fileProvider;
    private final UsersSpecification usersSpecification;
    private final FeedService feedService;
    private final FeedSpecification feedSpecification;
    private final PhotosService photosService;
    private final FeedMapper feedMapper;

    @Transactional(rollbackFor = Exception.class)
    public void register(String token, FeedCommand.RegisterCommand registerCommand, MultipartFile file) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        Users users = usersSpecification.findByUsersId(usersId);

        // 대표 아이콘 처리 확인
        if (registerCommand.isMain())
            feedService.updateMain(usersId, true);

        Feed insertFeed = registerCommand.toEntity(usersId);
        Feed newFeed = feedService.register(insertFeed);

        feedPhotoUpload(file, users, newFeed);
    }

    public FeedDTO.FeedInfoResponse info(String token, UUID feedId) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        Users users = usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedId(feedId);

        List<PhotosInfo.Info> photosInfoList = photosService.findAllByTargetIdOrderByCreatedAtDesc(feed.getFeedId());

        FeedInfo.FeedInfoDTO feedInfoDTO = FeedInfo.FeedInfoDTO.toDTO(feed, usersId, photosInfoList, users.getPhotosLimit());

        return new FeedDTO.FeedInfoResponse(
            users.getName(),
            feedInfoDTO
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void infoUpdate(String token, FeedCommand.InfoUpdateCommand infoUpdateCommand, MultipartFile file) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        Users users = usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedIdAndIsMind(infoUpdateCommand.feedId(), usersId);

        feed.infoUpdate(infoUpdateCommand);

        // 대표 아이콘 처리 확인
        if (infoUpdateCommand.isMain()) {
            feedService.updateMain(usersId, true);
            feed.updateIsMainTrue();
        }

        // 파일 삭제
        if (!CollectionUtils.isEmpty(infoUpdateCommand.deletePhotoTokens())) {
            Optional.of(infoUpdateCommand.deletePhotoTokens()).ifPresent(tokens -> tokens.forEach(photoToken -> {
                Photos photos = photosService.findByToken(photoToken);
                if (photos != null) {
                    fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                    photosService.delete(photos);
                    users.photosLimitMinus();
                }
            }));
        }

        // 파일 등록
        feedPhotoUpload(file, users, feed);
    }

    private void feedPhotoUpload(MultipartFile file, Users users, Feed feed) {
        Optional.ofNullable(file)
            .map(files -> {
                if (users.getPhotosLimit() + 1 == 20) throw new BadRequestException("더 이상 사진을 등록할 수 없습니다.");
                return fileProvider.uploadFile("feed", files);
            })
            .ifPresent(fileInfoDTO -> {
                Photos photos = fileInfoDTO.toPhotos(DefinedCode.C000600001, null, feed.getFeedId(), fileInfoDTO);
                photosService.register(photos);
                users.photosLimitPlus();
            });
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String token, UUID feedId) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        Users users = usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedIdAndIsMind(feedId, usersId);

        List<PhotosInfo.Info> photosInfoList = photosService.findAllByTargetIdOrderByCreatedAtDesc(feed.getFeedId());

        Optional.of(photosInfoList).ifPresent(photosInfos -> photosInfos.forEach(photosInfo -> {
            Photos photos = photosService.findByToken(photosInfo.token());
            if (photos != null) {
                fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                photosService.delete(photos);
                users.photosLimitMinus();
            }
        }));

        feedService.delete(feed);
    }

    @Transactional(readOnly = true)
    public FeedDTO.HomeResponse homeList(String token, FeedCommand.HomeCommand homeCommand) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        usersSpecification.findByUsersId(usersId);

        // 이전달
        List<List<FeedInfo.HomeInfo.WeeklyFeed>> prevWeeklyFeeds = feedService.weeklyList(usersId, homeCommand.getDate().minusMonths(1));

        // 현재
        List<List<FeedInfo.HomeInfo.WeeklyFeed>> weeklyFeeds = feedService.weeklyList(usersId, homeCommand.getDate());

        // 다음달
        List<List<FeedInfo.HomeInfo.WeeklyFeed>> nextWeeklyFeeds = feedService.weeklyList(usersId, homeCommand.getDate().plusMonths(1));

        LinkedHashMap<String, List<List<FeedInfo.HomeInfo.WeeklyFeed>>> weeklyMap = new LinkedHashMap<>();
        weeklyMap.put(homeCommand.getDate().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")), prevWeeklyFeeds);
        weeklyMap.put(homeCommand.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM")), weeklyFeeds);
        weeklyMap.put(homeCommand.getDate().plusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM")), nextWeeklyFeeds);

        // 목록 피드
        Page<FeedInfo.HomeFeedItemDTO> feedList = feedService.findAllByUsersIdAndTime(usersId, homeCommand);
        PagingDTO pagingDTO = feedMapper.toPagingDTO(feedList);
        List<FeedInfo.HomeFeedItem> homeFeedItemList = feedMapper.toConvertDTO(feedList.getContent());

        LinkedHashMap<DefinedCode, List<FeedInfo.HomeFeedItem>> dailyFeed = new LinkedHashMap<>();
        dailyFeed.put(DefinedCode.C000300001, homeFeedItemList.stream().filter(item -> item.tag().equals(DefinedCode.C000300001)).collect(Collectors.toList()));
        dailyFeed.put(DefinedCode.C000300002, homeFeedItemList.stream().filter(item -> item.tag().equals(DefinedCode.C000300002)).collect(Collectors.toList()));
        dailyFeed.put(DefinedCode.C000300003, homeFeedItemList.stream().filter(item -> item.tag().equals(DefinedCode.C000300003)).collect(Collectors.toList()));
        dailyFeed.put(DefinedCode.C000300004, homeFeedItemList.stream().filter(item -> item.tag().equals(DefinedCode.C000300004)).collect(Collectors.toList()));

        return new FeedDTO.HomeResponse(
            pagingDTO,
            weeklyMap,
            dailyFeed
        );
    }

}
