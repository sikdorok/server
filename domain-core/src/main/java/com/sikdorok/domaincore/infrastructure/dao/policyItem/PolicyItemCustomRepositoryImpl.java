package com.sikdorok.domaincore.infrastructure.dao.policyItem;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.photos.QPhotos;
import com.sikdorok.domaincore.model.policyItem.PolicyItemInfo;
import com.sikdorok.domaincore.model.policyItem.QPolicyItem;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class PolicyItemCustomRepositoryImpl implements PolicyItemCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final QPolicyItem policyItem = QPolicyItem.policyItem;
    private final QPhotos photos = QPhotos.photos;

    @Override
    public List<PolicyItemInfo.PolicyItem> getList(DefinedCode type) {
        return queryFactory
            .selectFrom(policyItem)
            .leftJoin(photos).on(photos.subType.eq(policyItem.type).and(photos.targetId.eq(policyItem.policyItemId)))
            .where(policyItem.type.eq(type))
            .orderBy(policyItem.sort.asc())
            .transform(
                groupBy(policyItem.policyItemId)
                    .list(
                        Projections.constructor(
                            PolicyItemInfo.PolicyItem.class,
                            policyItem.policyItemId,
                            policyItem.type,
                            policyItem.data,
                            policyItem.sort,
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

}
