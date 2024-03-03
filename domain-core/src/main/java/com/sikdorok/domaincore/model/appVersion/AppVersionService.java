package com.sikdorok.domaincore.model.appVersion;

import com.sikdorok.domaincore.model.shared.DefinedCode;

public interface AppVersionService {

    void register(AppVersionCommand.AppVersionRegister appVersionRegister);

    AppVersionInfo.AppVersionDTO latest(DefinedCode type);

}
