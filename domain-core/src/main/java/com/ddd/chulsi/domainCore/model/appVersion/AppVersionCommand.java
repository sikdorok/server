package com.ddd.chulsi.domainCore.model.appVersion;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

public class AppVersionCommand {

    public record AppVersionRegister(
        DefinedCode type,
        int major,
        int minor,
        int patch,
        boolean forceUpdateStatus
    ) {
        public static AppVersionRegister toCommand(DefinedCode type, int major, int minor, int patch, boolean forceUpdateStatus) {
            return new AppVersionRegister(type, major, minor, patch, forceUpdateStatus);
        }
    }

}
