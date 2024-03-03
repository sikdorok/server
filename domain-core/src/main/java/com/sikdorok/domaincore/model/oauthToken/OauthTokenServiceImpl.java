package com.sikdorok.domaincore.model.oauthToken;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthTokenServiceImpl implements OauthTokenService {

    private final OauthTokenReader oauthTokenReader;
    private final OauthTokenStore oauthTokenStore;

    @Override
    public void save(OauthToken oauthToken) {
        oauthTokenStore.save(oauthToken);
    }

    @Override
    public void delete(OauthToken oauthToken) {
        oauthTokenStore.delete(oauthToken);
    }

    @Override
    public OauthToken findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId) {
        return oauthTokenReader.findByOauthTypeAndOauthId(oauthType, oauthId);
    }
}
