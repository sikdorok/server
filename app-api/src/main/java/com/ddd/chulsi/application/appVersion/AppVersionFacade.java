package com.ddd.chulsi.application.appVersion;

import com.ddd.chulsi.domainCore.model.appVersion.AppVersionCommand;
import com.ddd.chulsi.domainCore.model.appVersion.AppVersionService;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import com.ddd.chulsi.infrastructure.jwt.JWTProperties;
import com.ddd.chulsi.infrastructure.jwt.JwtTokenUtil;
import com.ddd.chulsi.presentation.appVersion.dto.AppVersionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppVersionFacade {

    private final JwtTokenUtil jwtTokenUtil;
    private final JWTProperties properties;

    private final AppVersionService appVersionService;

    @Transactional(rollbackFor = Exception.class)
    public void register(String token, AppVersionCommand.AppVersionRegister appVersionRegister) {
        jwtTokenUtil.checkAuthIsManager(token, properties);
        appVersionService.register(appVersionRegister);
    }

    @Transactional(readOnly = true)
    public AppVersionDTO.AppVersionInfoResponse latest(DefinedCode type) {
        return new AppVersionDTO.AppVersionInfoResponse(appVersionService.latest(type));
    }

}
