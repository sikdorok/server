package com.ddd.chulsi.presentation.appVersion.dto;

import com.ddd.chulsi.domainCore.model.appVersion.AppVersionCommand;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AppVersionDTO {

    public record AppVersionRegisterRequest(
        @NotNull
        DefinedCode type,

        @Min(1)
        int major,

        @Min(1)
        int minor,

        @Min(1)
        int patch,

        @NotNull
        boolean forceUpdateStatus
    ) {
        public AppVersionCommand.AppVersionRegister toCommand() {
            return new AppVersionCommand.AppVersionRegister(type, major, minor, patch, forceUpdateStatus);
        }
    }

}
