package com.ddd.chulsi.presentation.home;

import com.ddd.chulsi.application.feed.FeedFacade;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping(value = "/home", name = "홈")
@RequiredArgsConstructor
public class HomeController {

    private final FeedFacade feedFacade;

    @GetMapping(value = "/monthly", name = "월 단위 목록")
    public BaseResponse<FeedDTO.MonthlyResponse> monthly(
        @AuthToken String token,
        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return BaseResponse.ofSuccess(feedFacade.monthly(token, date));
    }

    @GetMapping(value = "/list", name = "태그 목록 조회")
    public BaseResponse<FeedDTO.ListResponse> list(
        @AuthToken String token,
        @Valid FeedDTO.ListRequest listRequest
    ) {
        FeedCommand.ListCommand listCommand = listRequest.toCommand();
        return BaseResponse.ofSuccess(feedFacade.list(token, listCommand));
    }

    @GetMapping(value = "/list-view", name = "리스트뷰 목록 조회")
    public BaseResponse<FeedDTO.ListViewResponse> listView(
        @AuthToken String token,
        @Valid FeedDTO.ListViewRequest listViewRequest
    ) {
        FeedCommand.ListViewCommand listViewCommand = listViewRequest.toCommand();
        return BaseResponse.ofSuccess(feedFacade.listView(token, listViewCommand));
    }

}
