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
import com.ddd.chulsi.infrastructure.exception.ExistsException;
import com.ddd.chulsi.infrastructure.jwt.JWTClaim;
import com.ddd.chulsi.infrastructure.jwt.JWTProperties;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.infrastructure.mapper.feed.FeedMapper;
import com.ddd.chulsi.infrastructure.specification.feed.FeedSpecification;
import com.ddd.chulsi.infrastructure.specification.users.UsersSpecification;
import com.ddd.chulsi.infrastructure.util.CollectionUtils;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.PagingDTO;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

        Users users = usersSpecification.findByUsersId(usersId);

        duplicateCheck(
            registerCommand.time(),
            registerCommand.tag(),
            registerCommand.icon(),
            registerCommand.memo()
        );

        // 대표 아이콘 처리 확인
        if (registerCommand.isMain())
            feedService.updateMain(usersId, true, registerCommand.time());

        Feed insertFeed = registerCommand.toEntity(usersId);
        Feed newFeed = feedService.register(insertFeed);

        feedPhotoUpload(file, users, newFeed);

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

        Users users = usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedIdAndIsMind(infoUpdateCommand.feedId(), usersId);

        feed.infoUpdate(infoUpdateCommand);

        // 대표 아이콘 처리 확인
        if (infoUpdateCommand.isMain()) {
            feedService.updateMain(usersId, true, infoUpdateCommand.time());
            feed.updateIsMainTrue();
        } else feed.updateIsMainFalse();

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

        return feed.getFeedId();
    }

    private void feedPhotoUpload(MultipartFile file, Users users, Feed feed) {
        Optional.ofNullable(file)
            .map(files -> {
                if (users.getPhotosLimit() + 1 == 20) throw new BadRequestException("더 이상 사진을 등록할 수 없습니다.");
                return fileProvider.uploadFile("feed", files);
            })
            .ifPresent(fileInfoDTO -> {
                Photos photos;
                try {
                    photos = fileInfoDTO.get().toPhotos(DefinedCode.C000600001, null, feed.getFeedId());
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
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
