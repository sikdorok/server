package com.ddd.chulsi.domainCore.model.photos;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotosServiceImpl implements PhotosService {

    private final PhotosReader photosReader;
    private final PhotosStore photosStore;

    @Override
    public void register(Photos photos) {
        photosStore.save(photos);
    }

    @Override
    public List<PhotosInfo.Info> findAllByTargetIdOrderByCreatedAtDesc(UUID feedId) {
        return photosReader.findAllByTargetIdOrderByCreatedAtDesc(feedId);
    }

    @Override
    public Photos findByToken(UUID token) {
        return photosReader.findByToken(token);
    }

    @Override
    public void delete(Photos photos) {
        photosStore.delete(photos);
    }

    @Override
    public List<Photos> findByTypeAndSubType(DefinedCode type, DefinedCode subType) {
        return photosReader.findByTypeAndSubType(type, subType);
    }
}
