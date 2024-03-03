package com.sikdorok.domaincore.model.appVersion;

import com.sikdorok.domaincore.model.shared.DefinedCode;

public interface AppVersionReader {

    AppVersion findByTypeAndMajorAndMinorAndPatch(DefinedCode type, int major, int minor, int patch);

    AppVersion latest(DefinedCode type);

}
