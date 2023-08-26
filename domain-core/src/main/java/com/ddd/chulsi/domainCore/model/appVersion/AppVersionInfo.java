package com.ddd.chulsi.domainCore.model.appVersion;

public class AppVersionInfo {

    public record AppVersionDTO(
        String appInfoAppVersion,
        int major,
        int minor,
        int patch,
        boolean forceUpdateStatus
    ) {
        public static AppVersionDTO toDto(AppVersion appVersion) {
            return new AppVersionDTO(
                appVersion.getAppInfoAppVersion(),
                appVersion.getMajor(),
                appVersion.getMinor(),
                appVersion.getPatch(),
                appVersion.isForceUpdateStatus()
            );
        }
    }

}
