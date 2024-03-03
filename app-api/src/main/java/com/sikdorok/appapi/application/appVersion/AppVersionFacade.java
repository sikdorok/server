package com.sikdorok.appapi.application.appVersion;

import com.sikdorok.domaincore.model.appVersion.AppVersionCommand;
import com.sikdorok.domaincore.model.appVersion.AppVersionService;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import com.sikdorok.appapi.infrastructure.jwt.JWTProperties;
import com.sikdorok.appapi.infrastructure.jwt.JwtTokenUtil;
import com.sikdorok.appapi.presentation.appVersion.dto.AppVersionDTO;
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
