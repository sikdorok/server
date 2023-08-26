package com.ddd.chulsi.infrastructure.inMemory.appVersion;

import com.ddd.chulsi.domainCore.model.appVersion.AppVersionInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.presentation.appVersion.dto.AppVersionDTO;

public class AppVersionFactory {

    public static AppVersionDTO.AppVersionRegisterRequest givenAppVersionRegisterRequest() {
        return new AppVersionDTO.AppVersionRegisterRequest(
            DefinedCode.C000700001,
            1,
            0,
            0,
            false
        );
    }

    public static AppVersionInfo.AppVersionDTO givenAppVersionDTO() {
        return new AppVersionInfo.AppVersionDTO(
            "1.0.0",
            1,
            0,
            0,
            false
        );
    }

    public static AppVersionDTO.AppVersionInfoResponse givenAppVersionInfoResponse() {
        return new AppVersionDTO.AppVersionInfoResponse(
            givenAppVersionDTO()
        );
    }

}
