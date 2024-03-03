package com.sikdorok.domaincore.model.oauthToken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthTokenStoreImpl implements OauthTokenStore {

    private final OauthTokenJpaRepository oauthTokenJpaRepository;

    @Override
    public void save(OauthToken oauthToken) {
        oauthTokenJpaRepository.save(oauthToken);
    }

    @Override
    public void delete(OauthToken oauthToken) {
        oauthTokenJpaRepository.delete(oauthToken);
    }
}
