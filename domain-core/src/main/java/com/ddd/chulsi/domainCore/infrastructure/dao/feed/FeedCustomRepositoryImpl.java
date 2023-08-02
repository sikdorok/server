package com.ddd.chulsi.domainCore.infrastructure.dao.feed;

import com.ddd.chulsi.domainCore.infrastructure.dao.shared.PagingRequest;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.feed.QFeed;
import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.photos.QPhotos;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QFeed feed = QFeed.feed;
    private final QPhotos photos = QPhotos.photos;

    @Override
    public Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.HomeCommand homeCommand) {
        Pageable pageable = new PagingRequest(homeCommand.getPage(), homeCommand.getSize()).of();

        int size = queryFactory
            .select(feed)
            .from(feed)
            .where(
                feed.usersId.eq(usersId),
                feed.time.between(LocalDateTime.of(homeCommand.getDate(), LocalTime.of(0, 0)), LocalDateTime.of(homeCommand.getDate(), LocalTime.of(23, 59)))
            )
            .fetch().size();

        List<FeedInfo.HomeFeedItemDTO> feedInfoDTOList = queryFactory
            .selectFrom(feed)
            .leftJoin(photos).on(photos.type.eq(DefinedCode.C000600001).and(photos.targetId.eq(feed.feedId)))
            .where(
                feed.usersId.eq(usersId),
                feed.time.between(LocalDateTime.of(homeCommand.getDate(), LocalTime.of(0, 0)), LocalDateTime.of(homeCommand.getDate(), LocalTime.of(23, 59)))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(feed.time)
                    .list(
                        Projections.constructor(
                            FeedInfo.HomeFeedItemDTO.class,
                            list(
                                Projections.constructor(
                                    PhotosInfo.Info.class,
                                    photos.token,
                                    photos.uploadFullPath
                                )
                            ),
                            feed.icon,
                            feed.isMain,
                            feed.tag,
                            feed.time,
                            feed.memo
                        )
                    )
            );

        return new PageImpl<>(feedInfoDTOList, pageable, size);
    }

}
