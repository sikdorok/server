package com.ddd.chulsi.presentation.appVersion;

import com.ddd.chulsi.application.appVersion.AppVersionFacade;
import com.ddd.chulsi.domainCore.model.appVersion.AppVersion;
import com.ddd.chulsi.domainCore.model.appVersion.AppVersionCommand;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.annotation.AuthToken;
import com.ddd.chulsi.presentation.appVersion.dto.AppVersionDTO;
import com.ddd.chulsi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

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

    @PutMapping(name = "수정")
    public BaseResponse<Void> appVersionInfoUpdate(
        @AuthToken String token,
        @RequestBody @Valid AppVersionDTO.AppVersionInfoUpdateRequest request
    ) {
        AppVersionCommand.AppVersionInfoUpdate initAppVersionInfoUpdate = request.toCommand();
        appVersionFacade.infoUpdate(token, initAppVersionInfoUpdate);
        return BaseResponse.ofSuccess();
    }

}
