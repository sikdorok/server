package com.ddd.chulsi.domainCore.model.photos;

import java.util.List;
import java.util.UUID;

public interface PhotosService {

    void register(Photos photos);

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

    Photos findByPhotosId(UUID photosId);

    void delete(Photos photos);

}
