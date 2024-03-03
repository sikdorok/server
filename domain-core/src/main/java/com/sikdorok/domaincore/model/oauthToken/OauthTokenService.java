package com.sikdorok.domaincore.model.oauthToken;

import com.sikdorok.domaincore.model.shared.DefinedCode;

public interface OauthTokenService {

    void save(OauthToken oauthToken);

    void delete(OauthToken oauthToken);

    OauthToken findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

}
