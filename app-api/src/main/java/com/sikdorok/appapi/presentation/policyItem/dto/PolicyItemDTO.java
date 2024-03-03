package com.sikdorok.appapi.presentation.policyItem.dto;

import com.sikdorok.domaincore.model.photos.PhotosInfo;
import com.sikdorok.domaincore.model.policyItem.PolicyItemCommand;
import com.sikdorok.domaincore.model.policyItem.PolicyItemInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.exception.BadRequestException;
import com.sikdorok.appapi.infrastructure.exception.message.ErrorMessage;
import com.sikdorok.appapi.presentation.shared.request.Validator;
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
