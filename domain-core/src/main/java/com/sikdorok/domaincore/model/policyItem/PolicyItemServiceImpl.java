package com.sikdorok.domaincore.model.policyItem;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicyItemServiceImpl implements PolicyItemService {

    private final PolicyItemReader policyItemReader;
    private final PolicyItemStore policyItemStore;

    @Override
    public PolicyItem register(PolicyItem policyItem) {
        return policyItemStore.register(policyItem);
    }

    @Override
    public PolicyItem findByPolicyItemId(UUID policyItemId) {
        return policyItemReader.findByPolicyItemId(policyItemId);
    }

    @Override
    public void delete(PolicyItem policyItem) {
        policyItemStore.delete(policyItem);
    }

    @Override
    public List<PolicyItemInfo.PolicyItem> getList(DefinedCode type) {
        return policyItemReader.getList(type);
    }
}
