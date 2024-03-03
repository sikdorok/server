package com.sikdorok.domaincore.model.oauthToken;

public interface OauthTokenStore {

    void save(OauthToken oauthToken);

    void delete(OauthToken oauthToken);

}
