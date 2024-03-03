package com.sikdorok.domaincore.model.photos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotosStoreImpl implements PhotosStore {

    private final PhotosJpaRepository photosJpaRepository;

    @Override
    public void save(Photos photos) {
        photosJpaRepository.save(photos);
    }

    @Override
    public void delete(Photos photos) {
        photosJpaRepository.delete(photos);
    }
}
