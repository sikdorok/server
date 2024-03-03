package com.sikdorok.domaincore.model.oauthToken;

import com.sikdorok.domaincore.model.shared.DefinedCode;

public interface OauthTokenReader {

    OauthToken findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

}
