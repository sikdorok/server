package com.ddd.chulsi.domainCore.model.oauthToken;

public interface OauthTokenStore {

    void save(OauthToken oauthToken);

    void delete(OauthToken oauthToken);

}
