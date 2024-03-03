package com.sikdorok.domaincore.model.photos;

import com.sikdorok.domaincore.infrastructure.dao.photos.PhotosCustomRepository;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotosReaderImpl implements PhotosReader {

    private final PhotosJpaRepository photosJpaRepository;
    private final PhotosCustomRepository photosCustomRepository;

    @Override
    public List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId) {
        return photosCustomRepository.findAllByTargetIdOrderByCreatedAtDesc(feedId);
    }

    @Override
    public Photos findByToken(UUID token) {
        return photosJpaRepository.findByToken(token).orElse(null);
    }

    @Override
    public List<Photos> findByTypeAndSubType(DefinedCode type, DefinedCode subType) {
        return photosJpaRepository.findByTypeAndSubType(type, subType);
    }
}
