package com.sikdorok.appapi.application.feed;

import com.sikdorok.appapi.infrastructure.aws.FileInfoDTO;
import com.sikdorok.domaincore.model.feed.Feed;
import com.sikdorok.domaincore.model.feed.FeedCommand;
import com.sikdorok.domaincore.model.feed.FeedInfo;
import com.sikdorok.domaincore.model.feed.FeedService;
import com.sikdorok.domaincore.model.photos.Photos;
import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.photos.PhotosService;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.domaincore.model.users.Users;
import com.sikdorok.appapi.infrastructure.aws.FileProvider;
import com.sikdorok.appapi.infrastructure.exception.BadRequestException;
import com.sikdorok.appapi.infrastructure.exception.ExistsException;
import com.sikdorok.appapi.infrastructure.jwt.JWTClaim;
import com.sikdorok.appapi.infrastructure.jwt.JWTProperties;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.infrastructure.mapper.feed.FeedMapper;
import com.sikdorok.appapi.infrastructure.specification.feed.FeedSpecification;
import com.sikdorok.appapi.infrastructure.specification.users.UsersSpecification;
import com.sikdorok.system.CollectionUtils;
import com.sikdorok.appapi.presentation.feed.dto.FeedDTO;
import com.sikdorok.appapi.presentation.shared.response.dto.PagingDTO;
import com.sikdorok.system.Utils;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

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

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    public UUID register(String token, FeedCommand.RegisterCommand registerCommand, MultipartFile file) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        usersSpecification.findByUsersId(usersId);

        duplicateCheck(
            registerCommand.time(),
            registerCommand.tag(),
            registerCommand.icon(),
            registerCommand.memo()
        );

        // 대표 아이콘 처리 확인
        if (registerCommand.isMain())
            feedService.allDisableIsMain(usersId, registerCommand.time());

        Feed insertFeed = registerCommand.toEntity(usersId);
        Feed newFeed = feedService.register(insertFeed);

        feedPhotoUpload(file, newFeed);

        return newFeed.getFeedId();
    }

    private void duplicateCheck(LocalDateTime time, DefinedCode tag, DefinedCode icon, String memo) {
        // main 빼고 모든 데이터가 완전 동일한 것은 등록 불가
        if (
            feedService.duplicateCheck(
                time,
                tag,
                icon,
                memo
            )
        )
            throw new ExistsException();
    }

    @Transactional(readOnly = true)
    public FeedDTO.FeedInfoResponse info(String token, UUID feedId) {
        Feed feed = feedSpecification.findByFeedId(feedId);

        boolean isMine = false;
        UUID usersId = null;
        int photosLimit = 0;

        if (StringUtils.isNotBlank(token)) {
            if (token.startsWith(JwtTokenUtil.PREFIX)) token = token.replace(JwtTokenUtil.PREFIX, "");
            JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);
            usersId = jwtClaim.getUsersId();
            if (feed.getUsersId().equals(usersId)) isMine = true;
        }

        if (usersId == null) usersId = feed.getUsersId();

        Users users = usersSpecification.findByUsersId(usersId);

        if (isMine) photosLimit = users.getPhotosLimit();

        List<PhotosInfo.Info> photosInfoList = photosService.findAllByTargetIdOrderByCreatedAtDesc(feed.getFeedId());

        FeedInfo.FeedInfoDTO feedInfoDTO = FeedInfo.FeedInfoDTO.toDTO(feed, isMine, photosInfoList, photosLimit);

        return new FeedDTO.FeedInfoResponse(
            users.getName(),
            feedInfoDTO
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public UUID infoUpdate(String token, FeedCommand.InfoUpdateCommand infoUpdateCommand, MultipartFile file) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedIdAndIsMind(infoUpdateCommand.feedId(), usersId);

        feed.infoUpdate(infoUpdateCommand);

        // 대표 아이콘 처리 확인
        if (infoUpdateCommand.isMain()) {
            feedService.allDisableIsMain(usersId, infoUpdateCommand.time());
            feed.updateIsMainTrue();
        } else feed.updateIsMainFalse();

        // 파일 삭제
        if (!CollectionUtils.isEmpty(infoUpdateCommand.deletePhotoTokens())) {
            Optional.of(infoUpdateCommand.deletePhotoTokens()).ifPresent(tokens -> tokens.forEach(photoToken -> {
                Photos photos = photosService.findByToken(photoToken);
                if (photos != null) {
                    fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                    photosService.delete(photos);
                }
            }));
        }

        // 파일 등록
        feedPhotoUpload(file, feed);

        return feed.getFeedId();
    }

    private void feedPhotoUpload(MultipartFile file, Feed feed) {
        if (file != null && !file.isEmpty()) {
            if (file.getSize() >= 10 * 1024 * 1024) {
                log.error("Failed file size : {}", file.getSize());
                throw new MaxUploadSizeExceededException(10 * 1024 * 1024);
            }

            FileInfoDTO fileInfoDTO = fileProvider.uploadFile("feed", file);
            Photos photos = fileInfoDTO.toPhotos(DefinedCode.C000600001, null, feed.getFeedId());
            photosService.register(photos);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String token, UUID feedId) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedIdAndIsMind(feedId, usersId);

        List<PhotosInfo.Info> photosInfoList = photosService.findAllByTargetIdOrderByCreatedAtDesc(feed.getFeedId());

        Optional.of(photosInfoList).ifPresent(photosInfos -> photosInfos.forEach(photosInfo -> {
            Photos photos = photosService.findByToken(photosInfo.token());
            if (photos != null) {
                fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                photosService.delete(photos);
            }
        }));

        feedService.delete(feed);
    }

    @Transactional(readOnly = true)
    public FeedDTO.MonthlyResponse monthly(String token, LocalDate date) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();
        usersSpecification.findByUsersId(usersId);

        return new FeedDTO.MonthlyResponse(
            date,
            feedService.weeklyList(usersId, date)
        );
    }

    @Transactional(readOnly = true)
    public FeedDTO.ListResponse list(String token, FeedCommand.ListCommand listCommand) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();
        usersSpecification.findByUsersId(usersId);

        // 검색날짜 기준 태그 목록 조회
        List<DefinedCode> tags = feedService.getOnlyTags(usersId, listCommand.date());
        Collections.sort(tags);
        DefinedCode initTag = tags.stream().filter(Objects::nonNull).findFirst().orElse(DefinedCode.C000300001);

        Page<FeedInfo.HomeFeedItemDTO> feedList = feedService.findAllByUsersIdAndTime(usersId, listCommand, initTag);
        PagingDTO paging = feedMapper.toPagingDTO(feedList);
        List<FeedInfo.HomeFeedItem> dailyFeeds = feedMapper.toConvertDTO(feedList.getContent());

        return new FeedDTO.ListResponse(
            paging,
            dailyFeeds,
            initTag,
            tags
        );
    }

    @Transactional(readOnly = true)
    public FeedDTO.ListViewResponse listView(String token, FeedCommand.ListViewCommand listViewCommand) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();
        usersSpecification.findByUsersId(usersId);

        // nextCursorDate 값이 null 일 수도 있음
        // 기록물이 size 만큼 없는 경우
        String nextCursorDate = feedService.nextCursorDate(usersId, listViewCommand);

        List<FeedInfo.HomeListViewFeedItemDTO> feedList = feedService.listViewWithCursorBased(usersId, listViewCommand, nextCursorDate);
        List<FeedInfo.HomeListViewFeedItem> homeListViewFeedItemList = feedList != null ? feedMapper.toConvertDTOWithCursorBased(feedList) : Collections.emptyList();

        List<FeedInfo.HomeListViewDTO> dailyFeeds = new ArrayList<>();

        String flag = null;
        for (FeedInfo.HomeListViewFeedItem item : homeListViewFeedItemList) {
            String date = item.date();
            if (flag != null && flag.equals(date)) continue;
            flag = date;

            Map<String, List<FeedInfo.HomeListViewFeedItem>> map = new LinkedHashMap<>();

            List<FeedInfo.HomeListViewFeedItem> morning = new ArrayList<>(homeListViewFeedItemList.stream().filter(v -> v.date().equals(date) && v.tag() == DefinedCode.C000300001).toList());
            sortedFeedList(morning);

            List<FeedInfo.HomeListViewFeedItem> afternoon = new ArrayList<>(homeListViewFeedItemList.stream().filter(v -> v.date().equals(date) && v.tag() == DefinedCode.C000300002).toList());
            sortedFeedList(afternoon);

            List<FeedInfo.HomeListViewFeedItem> evening = new ArrayList<>(homeListViewFeedItemList.stream().filter(v -> v.date().equals(date) && v.tag() == DefinedCode.C000300003).toList());
            sortedFeedList(evening);

            List<FeedInfo.HomeListViewFeedItem> snack = new ArrayList<>(homeListViewFeedItemList.stream().filter(v -> v.date().equals(date) && v.tag() == DefinedCode.C000300004).toList());
            sortedFeedList(snack);

            map.put("morning", morning);
            map.put("afternoon", afternoon);
            map.put("evening", evening);
            map.put("snack", snack);

            dailyFeeds.add(new FeedInfo.HomeListViewDTO(date, map));
        }

        return new FeedDTO.ListViewResponse(
            nextCursorDate != null,
            nextCursorDate != null ? LocalDate.parse(nextCursorDate) : null,
            dailyFeeds
        );
    }

    private void sortedFeedList(List<FeedInfo.HomeListViewFeedItem> feedList) {
        if (feedList.isEmpty() || feedList.size() == 1) return;

        feedList.sort((o1, o2) -> {
            if (o1.originTime().isBefore(o2.originTime())) return -1;
            else if (o1.originTime().isAfter(o2.originTime())) return 1;
            else return 0;
        });
    }

    @EventListener
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void revokeUsers(FeedCommand.RevokeUsers revokeUsers) {
        List<FeedInfo.FeedSimpleInfo> feeds = feedService.findAllByUsersId(revokeUsers.usersId());

        if (!CollectionUtils.isEmpty(feeds)) {
            feeds.forEach(feedInfoDTO -> Optional.of(feedInfoDTO.photosInfoList()).ifPresent(photosInfos -> photosInfos.forEach(photosInfo -> {
                Photos photos = photosService.findByToken(photosInfo.token());
                if (photos != null) {
                    fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                    photosService.delete(photos);
                }
            })));
        }

        feedService.revokeUsers(revokeUsers.usersId());
    }

}
