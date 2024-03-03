package com.sikdorok.appapi.presentation.appVersion;

import com.sikdorok.appapi.application.appVersion.AppVersionFacade;
import com.sikdorok.domaincore.model.appVersion.AppVersionCommand;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.annotation.AuthToken;
import com.sikdorok.appapi.presentation.appVersion.dto.AppVersionDTO;
import com.sikdorok.appapi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/app-version", name = "앱 버전")
public class AppVersionController {

    private final AppVersionFacade appVersionFacade;

    @GetMapping(value = "{type}", name = "최신 정보 조회")
    public BaseResponse<AppVersionDTO.AppVersionInfoResponse> latest(
        @PathVariable @NotNull DefinedCode type
    ) {
        return BaseResponse.ofSuccess(appVersionFacade.latest(type));
    }

    @PostMapping(name = "등록")
    public BaseResponse<Void> register(
        @AuthToken String token,
        @RequestBody @Valid AppVersionDTO.AppVersionRegisterRequest request
    ) {
        AppVersionCommand.AppVersionRegister initAppVersionRegisterRequest = request.toCommand();
        appVersionFacade.register(token, initAppVersionRegisterRequest);
        return BaseResponse.ofSuccess();
    }

}
