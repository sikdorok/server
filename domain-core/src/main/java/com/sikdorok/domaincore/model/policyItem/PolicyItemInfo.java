package com.sikdorok.domaincore.model.policyItem;

import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public class PolicyItemInfo {

    public record PolicyItem(
        UUID policyItemId,
        DefinedCode type,
        String data,
        int sort,
        List<PhotosInfo.Info> photosInfoList
    ) {
    }

}
