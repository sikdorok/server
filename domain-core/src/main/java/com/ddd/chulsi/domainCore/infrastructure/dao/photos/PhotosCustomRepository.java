package com.ddd.chulsi.domainCore.infrastructure.dao.photos;

import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;

import java.util.List;
import java.util.UUID;

public interface PhotosCustomRepository {

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

}
