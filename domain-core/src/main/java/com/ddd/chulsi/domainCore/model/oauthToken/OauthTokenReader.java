package com.ddd.chulsi.domainCore.model.oauthToken;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

public interface OauthTokenReader {

    OauthToken findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId);

}
