package com.sikdorok.domaincore.model.policyItem;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyItemStoreImpl implements PolicyItemStore {

    private final PolicyItemJpaRepository policyItemJpaRepository;

    @Override
    public PolicyItem register(PolicyItem policyItem) {
        return policyItemJpaRepository.save(policyItem);
    }

    @Override
    public void delete(PolicyItem policyItem) {
        policyItemJpaRepository.delete(policyItem);
    }

}
