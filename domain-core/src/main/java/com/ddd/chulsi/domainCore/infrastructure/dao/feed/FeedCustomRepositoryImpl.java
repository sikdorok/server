package com.ddd.chulsi.domainCore.infrastructure.dao.feed;

import com.ddd.chulsi.domainCore.infrastructure.dao.shared.PagingRequest;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.domainCore.model.feed.FeedInfo;
import com.ddd.chulsi.domainCore.model.feed.QFeed;
import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.photos.QPhotos;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    public Page<FeedInfo.HomeFeedItemDTO> findAllByUsersIdAndTime(UUID usersId, FeedCommand.ListCommand listCommand) {
        Pageable pageable = new PagingRequest(listCommand.page(), listCommand.size()).of();

        JPAQuery<Long> size = queryFactory
            .select(feed.count())
            .from(feed)
            .where(
                feed.usersId.eq(usersId),
                feed.tag.eq(listCommand.tag()),
                feed.time.between(LocalDateTime.of(listCommand.date(), LocalTime.of(0, 0)), LocalDateTime.of(listCommand.date(), LocalTime.of(23, 59)))
            );

        List<FeedInfo.HomeFeedItemDTO> feedInfoDTOList = queryFactory
            .selectFrom(feed)
            .leftJoin(photos).on(photos.type.eq(DefinedCode.C000600001).and(photos.targetId.eq(feed.feedId)))
            .where(
                feed.usersId.eq(usersId),
                feed.tag.eq(listCommand.tag()),
                feed.time.between(LocalDateTime.of(listCommand.date(), LocalTime.of(0, 0)), LocalDateTime.of(listCommand.date(), LocalTime.of(23, 59)))
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(feed.time.asc())
            .transform(
                groupBy(feed.time)
                    .list(
                        Projections.constructor(
                            FeedInfo.HomeFeedItemDTO.class,
                            feed.feedId,
                            feed.icon,
                            feed.isMain,
                            feed.tag,
                            feed.time,
                            feed.memo,
                            list(
                                Projections.constructor(
                                    PhotosInfo.Info.class,
                                    photos.token,
                                    photos.uploadFullPath
                                )
                            )
                        )
                    )
            );

        return PageableExecutionUtils.getPage(feedInfoDTOList, pageable, size::fetchOne);
    }

    @Override
    public List<FeedInfo.HomeListViewFeedItemDTO> listViewWithCursorBased(UUID usersId, FeedCommand.ListViewCommand listViewCommand, String nextCursorDate) {
        return queryFactory
            .selectFrom(feed)
            .leftJoin(photos).on(photos.type.eq(DefinedCode.C000600001).and(photos.targetId.eq(feed.feedId)))
            .where(
                feed.usersId.eq(usersId),
                feed.time.year().eq(listViewCommand.date().getYear()).and(feed.time.month().eq(listViewCommand.date().getMonth().getValue())),
                cursorDate(listViewCommand.cursorDate(), nextCursorDate)
            )
            .orderBy(feed.time.desc(), feed.tag.asc())
            .transform(
                groupBy(feed.feedId)
                    .list(
                        Projections.constructor(
                            FeedInfo.HomeListViewFeedItemDTO.class,
                            feed.feedId,
                            feed.icon,
                            feed.isMain,
                            feed.tag,
                            feed.time,
                            feed.memo,
                            list(
                                Projections.constructor(
                                    PhotosInfo.Info.class,
                                    photos.token,
                                    photos.uploadFullPath
                                )
                            )
                        )
                    )
            );
    }

    @Override
    public String nextCursorDate(UUID usersId, FeedCommand.ListViewCommand listViewCommand) {
        return queryFactory
            .select(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d"))
            .from(feed)
            .where(
                feed.usersId.eq(usersId),
                feed.time.year().eq(listViewCommand.date().getYear()).and(feed.time.month().eq(listViewCommand.date().getMonth().getValue())),
                listViewCommand.cursorDate() != null ? Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d").loe(listViewCommand.cursorDate().toString()) : null
            )
            .groupBy(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d"))
            .orderBy(feed.time.desc())
            .offset(listViewCommand.size())
            .limit(1)
            .fetchOne();
    }

    private BooleanExpression cursorDate(LocalDate cursorDate, String nextCursorDate) {
        // 처음 조회
        if (cursorDate == null) {
            // 처음 조회지만 다음 조회할 내용이 없음 (row size 가 조회할 size 보다 작은 경우)
            if (nextCursorDate == null) return null;

            return Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d").gt(nextCursorDate);
        }
        // n 번째 조회
        else {
            // n 번째 조회지만 다음이 없음
            if (nextCursorDate == null)
                return Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d").loe(cursorDate.toString());

            return Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d").gt(nextCursorDate)
                .and(Expressions.stringTemplate("DATE_FORMAT({0}, {1})", feed.time, "%Y-%m-%d").loe(cursorDate.toString()));
        }
    }

}
