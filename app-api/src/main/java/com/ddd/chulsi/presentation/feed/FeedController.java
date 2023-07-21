package com.ddd.chulsi.presentation.feed;

import com.ddd.chulsi.application.feed.FeedFacade;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping(value = "/feed", name = "피드")
@RequiredArgsConstructor
public class FeedController {

    private final FeedFacade feedFacade;

//    @GetMapping(value = "/list", name = "정보 목록 조회")
//    public BaseResponse<FeedDTO.FeedListResponse> list(
//        @AuthToken String token,
//        @Valid FeedDTO.FeedListRequest request
//    ) {
//        FeedCommand.ListCommand listCommand = request.toCommand();
//        return BaseResponse.ofSuccess(feedFacade.list(token, listCommand));
//    }

    @GetMapping(value = "{feedId}", name = "정보 조회")
    public BaseResponse<FeedDTO.FeedInfoResponse> info(
        @AuthToken String token,
        @PathVariable @NotNull UUID feedId
    ) {
        return BaseResponse.ofSuccess(feedFacade.info(token, feedId));
    }

    @PostMapping(name = "등록", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> register(
        @AuthToken String token,
        @RequestPart @Valid FeedDTO.FeedRegisterRequest request,
        @RequestPart(required = false) MultipartFile file
    ) {
        FeedCommand.RegisterCommand registerCommand = request.toCommand();
        feedFacade.register(token, registerCommand, file);
        return BaseResponse.ofSuccess();
    }

    @PutMapping(name = "정보 수정", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<Void> feedInfoUpdate(
        @AuthToken String token,
        @RequestPart @Valid FeedDTO.FeedInfoUpdateRequest request,
        @RequestPart(required = false) MultipartFile file
    ) {
        FeedCommand.InfoUpdateCommand infoUpdateCommand = request.toCommand();
        feedFacade.infoUpdate(token, infoUpdateCommand, file);
        return BaseResponse.ofSuccess();
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
