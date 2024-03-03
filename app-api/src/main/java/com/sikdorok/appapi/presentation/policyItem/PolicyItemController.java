package com.sikdorok.appapi.presentation.policyItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sikdorok.appapi.application.policyItem.PolicyItemFacade;
import com.sikdorok.domaincore.model.policyItem.PolicyItemCommand;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.annotation.AuthToken;
import com.sikdorok.appapi.presentation.policyItem.dto.PolicyItemDTO;
import com.sikdorok.appapi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(value = "/policy-item", name = "정책")
@RequiredArgsConstructor
public class PolicyItemController {

    private final PolicyItemFacade policyItemFacade;

    @GetMapping(value = "/list/{type}", name = "목록 조회")
    public BaseResponse<PolicyItemDTO.PolicyItemListResponse> list(
        @PathVariable @NotBlank DefinedCode type
    ) {
        return BaseResponse.ofSuccess(policyItemFacade.list(type));
    }

    @GetMapping(value = "{policyItemId}", name = "조회")
    public BaseResponse<PolicyItemDTO.PolicyItemInfoResponse> info(
        @PathVariable @NotBlank UUID policyItemId
    ) {
        return BaseResponse.ofSuccess(policyItemFacade.info(policyItemId));
    }

    @PostMapping(name = "등록", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> register(
        @AuthToken String token,
        @RequestPart @Valid PolicyItemDTO.RegisterRequest request,
        @RequestPart(required = false) MultipartFile file
    ) throws JsonProcessingException {
        PolicyItemCommand.RegisterCommand initPolicyInfo = request.toCommand();
        policyItemFacade.register(token, initPolicyInfo, file);
        return BaseResponse.ofSuccess();
    }

    @PutMapping(name = "수정", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> policyItemInfoUpdate(
        @AuthToken String token,
        @RequestPart @Valid PolicyItemDTO.UpdateRequest request,
        @RequestPart(required = false) MultipartFile file
    ) {
        PolicyItemCommand.InfoUpdateCommand initPolicyInfo = request.toCommand();
        policyItemFacade.infoUpdate(token, initPolicyInfo, file);
        return BaseResponse.ofSuccess();
    }

    @DeleteMapping(value = "{policyItemId}", name = "삭제")
    public BaseResponse<Void> delete(
        @AuthToken String token,
        @PathVariable @NotBlank UUID policyItemId
    ) {
        policyItemFacade.delete(token, policyItemId);
        return BaseResponse.ofSuccess();
    }

}
