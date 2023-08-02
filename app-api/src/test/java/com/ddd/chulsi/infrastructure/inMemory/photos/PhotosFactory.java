package com.ddd.chulsi.infrastructure.inMemory.photos;

import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PhotosFactory {

    public static List<PhotosInfo.Info> givenPhotosInfoList() {
        return Arrays.asList(
            new PhotosInfo.Info(UUID.randomUUID(),"feed/1.png"),
            new PhotosInfo.Info(UUID.randomUUID(), "feed/2.png")
        );
    }

}
