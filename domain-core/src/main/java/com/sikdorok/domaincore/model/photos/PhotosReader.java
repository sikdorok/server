package com.sikdorok.domaincore.model.photos;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PhotosReader {

    List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId);

    Photos findByToken(UUID token);

    List<Photos> findByTypeAndSubType(DefinedCode type, DefinedCode subType);

}
