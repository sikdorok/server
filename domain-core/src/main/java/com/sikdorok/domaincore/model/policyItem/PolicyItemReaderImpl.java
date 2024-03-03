package com.sikdorok.domaincore.model.policyItem;

import com.sikdorok.domaincore.infrastructure.dao.policyItem.PolicyItemCustomRepository;
import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicyItemReaderImpl implements PolicyItemReader {

    private final PolicyItemJpaRepository policyItemJpaRepository;
    private final PolicyItemCustomRepository policyItemCustomRepository;

    @Override
    public PolicyItem findByPolicyItemId(UUID policyItemId) {
        return policyItemJpaRepository.findById(policyItemId).orElse(null);
    }

    @Override
    public List<PolicyItemInfo.PolicyItem> getList(DefinedCode type) {
        return policyItemCustomRepository.getList(type);
    }
}
