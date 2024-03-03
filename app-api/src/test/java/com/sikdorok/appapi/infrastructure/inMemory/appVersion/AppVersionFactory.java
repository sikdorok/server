package com.sikdorok.appapi.infrastructure.inMemory.appVersion;

import com.sikdorok.domaincore.model.appVersion.AppVersionInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.presentation.appVersion.dto.AppVersionDTO;

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
