package com.ddd.chulsi.infrastructure.inMemory.policyItem;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.presentation.policyItem.dto.PolicyItemDTO;

import java.util.Set;
import java.util.UUID;

import static com.ddd.chulsi.infrastructure.inMemory.photos.PhotosFactory.givenPhotosInfoList;

public class PolicyItemFactory {

    public static PolicyItemDTO.PolicyItemInfoResponse givenPolicyItemInfoResponse() {
        return new PolicyItemDTO.PolicyItemInfoResponse(
            UUID.randomUUID(),
            DefinedCode.C000500001,
            "내용",
        1,
            givenPhotosInfoList()
        );
    }

    public static PolicyItemDTO.RegisterRequest givenRegisterRequest() {
        return new PolicyItemDTO.RegisterRequest(
            DefinedCode.C000500001,
            "내용",
            1
        );
    }

    public static PolicyItemDTO.UpdateRequest givenUpdateRequest() {
        return new PolicyItemDTO.UpdateRequest(
            UUID.randomUUID(),
            DefinedCode.C000500001,
            "내용",
            1,
            Set.of(UUID.randomUUID(), UUID.randomUUID())
        );
    }

}
