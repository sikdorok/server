package com.ddd.chulsi.domainCore.model.appVersion;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

public interface AppVersionService {

    void register(AppVersionCommand.AppVersionRegister appVersionRegister);

    AppVersionInfo.AppVersionDTO latest(DefinedCode type);

}
