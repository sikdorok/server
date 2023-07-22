package com.ddd.chulsi.domainCore.infrastructure.dao.photos;

import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.photos.QPhotos;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
                photos.photosId,
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
