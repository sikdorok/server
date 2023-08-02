package com.ddd.chulsi.presentation.policyItem.dto;

import com.ddd.chulsi.domainCore.model.photos.PhotosInfo;
import com.ddd.chulsi.domainCore.model.policyItem.PolicyItemCommand;
import com.ddd.chulsi.domainCore.model.policyItem.PolicyItemInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.exception.BadRequestException;
import com.ddd.chulsi.infrastructure.exception.message.ErrorMessage;
import com.ddd.chulsi.presentation.shared.request.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PolicyItemDTO {

    public record RegisterRequest(
        @NotNull
        DefinedCode type,

        String data,

        @Min(1)
        int sort
    ) implements Validator {
        public PolicyItemCommand.RegisterCommand toCommand() {
            return new PolicyItemCommand.RegisterCommand(type, data, sort);
        }

        @Override
        public void verify() {
            if (!type.getSectionCode().equals(DefinedCode.C0005.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG, "type");
        }
    }

    public record UpdateRequest(
        @NotNull
        UUID policyItemId,

        @NotNull
        DefinedCode type,

        String data,

        @Min(1)
        int sort,
        Set<UUID> deletePhotoTokens
    ) implements Validator {
        public PolicyItemCommand.InfoUpdateCommand toCommand() {
            return new PolicyItemCommand.InfoUpdateCommand(policyItemId, type, data, sort, deletePhotoTokens);
        }

        @Override
        public void verify() {
            if (!type.getSectionCode().equals(DefinedCode.C0005.getSectionCode()))
                throw new BadRequestException(ErrorMessage.EXPECTATION_FAILED_MSG, "type");
        }
    }

    public record PolicyItemInfoResponse(
        UUID policyItemId,
        DefinedCode type,
        String data,
        int sort,
        List<PhotosInfo.Info> photosInfoList
    ) {
    }

    public record PolicyItemListResponse(
        List<PolicyItemInfo.PolicyItem> policyItemList
    ) {
    }
}
