package com.ddd.chulsi.domainCore.model.photos;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PhotosService {

    void register(Photos photos);

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

    Photos findByPhotosId(UUID photosId);

    void delete(Photos photos);

    Photos findByToken(UUID photosToken);

    List<Photos> findByTypeAndSubType(DefinedCode type, DefinedCode subType);

}
