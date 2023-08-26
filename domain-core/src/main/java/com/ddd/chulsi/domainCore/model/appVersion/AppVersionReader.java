package com.ddd.chulsi.domainCore.model.appVersion;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

public interface AppVersionReader {

    AppVersion findByTypeAndMajorAndMinorAndPatch(DefinedCode type, int major, int minor, int patch);

    AppVersion latest(DefinedCode type);

}
