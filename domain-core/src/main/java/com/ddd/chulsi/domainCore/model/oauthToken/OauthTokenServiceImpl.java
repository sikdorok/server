package com.ddd.chulsi.domainCore.model.oauthToken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {

    private final OauthTokenStore oauthTokenStore;

    @Override
    public void save(OauthToken oauthToken) {
        oauthTokenStore.save(oauthToken);
    }

    @Override
    public void delete(OauthToken oauthToken) {
        oauthTokenStore.delete(oauthToken);
    }
}
