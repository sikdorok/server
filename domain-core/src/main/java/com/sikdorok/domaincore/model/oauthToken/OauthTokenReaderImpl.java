package com.sikdorok.domaincore.model.oauthToken;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthTokenReaderImpl implements OauthTokenReader {

    private final OauthTokenJpaRepository oauthTokenJpaRepository;

    @Override
    public OauthToken findByOauthTypeAndOauthId(DefinedCode oauthType, Long oauthId) {
        return oauthTokenJpaRepository.findByOauthTypeAndOauthId(oauthType, oauthId).orElse(null);
    }

}
