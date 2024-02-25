package com.ddd.chulsi.infrastructure.aws;

import com.ddd.chulsi.domainCore.model.photos.Photos;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.util.UUID;

public record FileInfoDTO(
    String uploadPath,
    String uploadFileName,
    String originFileName,
    String uploadFullPath,
    String contentType,
    String ext,
    long size
) {
    public Photos toPhotos(DefinedCode type, DefinedCode subType, UUID targetId) {
        return Photos.builder()
            .type(type)
            .subType(subType)
            .token(UUID.randomUUID())
            .targetId(targetId)
            .uploadPath(uploadPath)
            .uploadFileName(uploadFileName)
            .originFileName(originFileName)
            .uploadFullPath(uploadFullPath)
            .contentType(contentType)
            .ext(ext)
            .size(size)
            .build();
    }
}
