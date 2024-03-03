package com.sikdorok.domaincore.model.photos;

import java.util.UUID;

public class PhotosInfo {

    public record Info (
        UUID token,
        String uploadFullPath
    ) {

    }

}
