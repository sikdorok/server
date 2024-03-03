package com.sikdorok.domaincore.model.photos;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PhotosService {

    void register(Photos photos);

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

    void delete(Photos photos);

    Photos findByToken(UUID token);

    List<Photos> findByTypeAndSubType(DefinedCode type, DefinedCode subType);

}
