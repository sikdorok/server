package com.ddd.chulsi.domainCore.model.policyItem;

import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

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
