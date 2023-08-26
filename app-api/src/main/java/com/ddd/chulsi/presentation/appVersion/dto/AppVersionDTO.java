package com.ddd.chulsi.presentation.appVersion.dto;

import com.ddd.chulsi.domainCore.model.appVersion.AppVersionCommand;
import com.ddd.chulsi.domainCore.model.appVersion.AppVersionInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AppVersionDTO {

    public record AppVersionRegisterRequest(
        @NotNull
        DefinedCode type,

        @Min(0)
        int major,

        @Min(0)
        int minor,

        @Min(0)
        int patch,

        @NotNull
        boolean forceUpdateStatus
    ) {
        public AppVersionCommand.AppVersionRegister toCommand() {
            return new AppVersionCommand.AppVersionRegister(type, major, minor, patch, forceUpdateStatus);
        }
    }

    public record AppVersionInfoResponse(
        AppVersionInfo.AppVersionDTO appVersion
    ) {
    }
}
