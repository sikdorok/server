package com.sikdorok.domaincore.infrastructure.dao.photos;

import com.sikdorok.domaincore.model.photos.PhotosInfo;

import java.util.List;
import java.util.UUID;

public interface PhotosCustomRepository {

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

}
