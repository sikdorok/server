package com.ddd.chulsi.domainCore.model.policyItem;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import io.micrometer.common.util.StringUtils;

import java.util.Set;
import java.util.UUID;

public class PolicyItemCommand {

    public record RegisterCommand(
        DefinedCode type,
        String data,
        int sort
    ) {
        public PolicyItem toEntity() {
            return PolicyItem.builder()
                .type(type)
                .data(StringUtils.isBlank(data) ? null : data)
                .sort(sort)
                .build();
        }
    }

    public record InfoUpdateCommand(
        UUID policyItemId,
        DefinedCode type,
        String data,
        int sort,
        Set<UUID> deletePhotoTokens
    ) {
    }

}
