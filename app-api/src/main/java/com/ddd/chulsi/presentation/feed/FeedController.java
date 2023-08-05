package com.ddd.chulsi.presentation.feed;

import com.ddd.chulsi.application.feed.FeedFacade;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(value = "/feed", name = "피드")
@RequiredArgsConstructor
public class FeedController {

    private final FeedFacade feedFacade;

    @GetMapping(value = "{feedId}", name = "정보 조회")
    public BaseResponse<FeedDTO.FeedInfoResponse> info(
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String token,
        @PathVariable @NotNull UUID feedId
    ) {
        return BaseResponse.ofSuccess(feedFacade.info(token, feedId));
    }

    @PostMapping(name = "등록", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UUID> register(
        @AuthToken String token,
        @RequestPart @Valid FeedDTO.FeedRegisterRequest request,
        @RequestPart(required = false) MultipartFile file
    ) {
        FeedCommand.RegisterCommand registerCommand = request.toCommand();
        return BaseResponse.ofSuccess(feedFacade.register(token, registerCommand, file));
    }

    @PutMapping(name = "정보 수정", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<UUID> feedInfoUpdate(
        @AuthToken String token,
        @RequestPart @Valid FeedDTO.FeedInfoUpdateRequest request,
        @RequestPart(required = false) MultipartFile file
    ) {
        FeedCommand.InfoUpdateCommand infoUpdateCommand = request.toCommand();
        return BaseResponse.ofSuccess(feedFacade.infoUpdate(token, infoUpdateCommand, file));
    }

    @DeleteMapping(value = "{feedId}", name = "정보 삭제")
    public BaseResponse<Void> delete(
        @AuthToken String token,
        @PathVariable @NotNull UUID feedId
    ) {
        feedFacade.delete(token, feedId);
        return BaseResponse.ofSuccess();
    }

}
