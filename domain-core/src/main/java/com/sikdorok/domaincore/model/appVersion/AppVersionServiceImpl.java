package com.sikdorok.domaincore.model.appVersion;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppVersionServiceImpl implements AppVersionService {

    private final AppVersionReader appVersionReader;
    private final AppVersionStore appVersionStore;

    @Override
    public void register(AppVersionCommand.AppVersionRegister appVersionRegister) {
        AppVersion appVersion = appVersionReader.findByTypeAndMajorAndMinorAndPatch(appVersionRegister.type(), appVersionRegister.major(), appVersionRegister.minor(), appVersionRegister.patch());
        if (appVersion == null) appVersionStore.register(appVersionRegister.toEntity());
        else {
            appVersion.updateInfo(appVersionRegister.major(), appVersionRegister.minor(), appVersionRegister.patch());
            if (appVersionRegister.forceUpdateStatus()) appVersion.forceUpdateEnable();
            else appVersion.forceUpdateDisable();
        }
    }

    @Override
    public AppVersionInfo.AppVersionDTO latest(DefinedCode type) {
        AppVersion latest = appVersionReader.latest(type);
        if (latest != null) return AppVersionInfo.AppVersionDTO.toDto(latest);
        return null;
    }

}
