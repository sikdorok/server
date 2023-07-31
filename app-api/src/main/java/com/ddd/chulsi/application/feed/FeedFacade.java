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
import com.ddd.chulsi.infrastructure.specification.feed.FeedSpecification;
import com.ddd.chulsi.infrastructure.specification.users.UsersSpecification;
import com.ddd.chulsi.infrastructure.util.CollectionUtils;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Transactional(rollbackOn = Exception.class)
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

    @Transactional(rollbackOn = Exception.class)
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
        if (!CollectionUtils.isEmpty(infoUpdateCommand.deletePhotoId())) {
            Optional.of(infoUpdateCommand.deletePhotoId()).ifPresent(photosIds -> photosIds.forEach(photosId -> {
                Photos photos = photosService.findByPhotosId(photosId);
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

    @Transactional(rollbackOn = Exception.class)
    public void delete(String token, UUID feedId) {
        JWTClaim jwtClaim = jwtTokenUtil.checkAuth(token, properties);

        UUID usersId = jwtClaim.getUsersId();

        Users users = usersSpecification.findByUsersId(usersId);

        Feed feed = feedSpecification.findByFeedIdAndIsMind(feedId, usersId);

        List<PhotosInfo.Info> photosInfoList = photosService.findAllByTargetIdOrderByCreatedAtDesc(feed.getFeedId());

        Optional.of(photosInfoList).ifPresent(photosInfos -> photosInfos.forEach(photosInfo -> {
            Photos photos = photosService.findByPhotosId(photosInfo.photosId());
            if (photos != null) {
                fileProvider.deleteFile(photos.getUploadPath() + "/" + photos.getUploadFileName());
                photosService.delete(photos);
                users.photosLimitMinus();
            }
        }));

        feedService.delete(feed);
    }
}
