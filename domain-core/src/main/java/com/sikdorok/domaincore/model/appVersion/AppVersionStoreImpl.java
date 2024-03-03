package com.sikdorok.domaincore.model.appVersion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppVersionStoreImpl implements AppVersionStore {

    private final AppVersionJpaRepository appVersionJpaRepository;

    @Override
    public void register(AppVersion appVersion) {
        appVersionJpaRepository.save(appVersion);
    }

}
