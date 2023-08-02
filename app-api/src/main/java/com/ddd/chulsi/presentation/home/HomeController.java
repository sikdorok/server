package com.ddd.chulsi.presentation.home;

import com.ddd.chulsi.application.feed.FeedFacade;
import com.ddd.chulsi.domainCore.model.feed.FeedCommand;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.presentation.feed.dto.FeedDTO;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/home", name = "홈")
@RequiredArgsConstructor
public class HomeController {

    private final FeedFacade feedFacade;

    @GetMapping
    public BaseResponse<FeedDTO.HomeResponse> home(
        @AuthToken String token,
        @Valid FeedDTO.HomeRequest homeRequest
    ) {
        FeedCommand.HomeCommand homeCommand = homeRequest.toCommand();
        return BaseResponse.ofSuccess(feedFacade.homeList(token, homeCommand));
    }

}
