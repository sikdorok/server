package com.ddd.chulsi.domainCore.model.appVersion;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppVersionReaderImpl implements AppVersionReader {

    private final AppVersionJpaRepository appVersionJpaRepository;

    @Override
    public AppVersion findByTypeAndMajorAndMinorAndPatch(DefinedCode type, int major, int minor, int patch) {
        return appVersionJpaRepository.findByTypeAndMajorAndMinorAndPatch(type, major, minor, patch).orElse(null);
    }

    @Override
    public AppVersion latest(DefinedCode type) {
        return appVersionJpaRepository.findFirstByTypeOrderByMajorDescMinorDescPatchDesc(type).orElse(null);
    }

}
