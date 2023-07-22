package com.ddd.chulsi.domainCore.model.photos;

import com.ddd.chulsi.domainCore.infrastructure.dao.photos.PhotosCustomRepository;
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
    public Photos findByPhotosId(UUID photosId) {
        return photosJpaRepository.findByPhotosId(photosId).orElse(null);
    }
}
