package com.sikdorok.domaincore.infrastructure.dao.photos;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.photos.QPhotos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PhotosCustomRepositoryImpl implements PhotosCustomRepository {

    private final JPAQueryFactory queryFactory;

    private final QPhotos photos = QPhotos.photos;

    @Override
    public List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId) {
        return queryFactory
            .select(Projections.constructor(
                PhotosInfo.Info.class,
                photos.token,
                photos.uploadFullPath
            ))
            .from(photos)
            .where(
                photos.targetId.eq(feedId)
            )
            .orderBy(photos.createdAt.desc())
            .fetch();
    }
}
