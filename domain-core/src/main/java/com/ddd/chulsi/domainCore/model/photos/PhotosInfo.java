package com.ddd.chulsi.domainCore.model.photos;

import java.util.UUID;

public class PhotosInfo {

    public record Info (
        UUID token,
        String uploadFullPath
    ) {

    }

}
