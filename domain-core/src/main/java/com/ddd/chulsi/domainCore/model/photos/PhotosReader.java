package com.ddd.chulsi.domainCore.model.photos;

import java.util.List;
import java.util.UUID;

public interface PhotosReader {

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

    Photos findByPhotosId(UUID photosId);

}
