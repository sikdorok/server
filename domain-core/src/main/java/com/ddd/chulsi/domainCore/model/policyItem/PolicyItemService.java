package com.ddd.chulsi.domainCore.model.policyItem;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PolicyItemService {

    PolicyItem register(PolicyItem policyItem);

    PolicyItem findByPolicyItemId(UUID policyItemId);

    void delete(PolicyItem policyItem);

    List<PolicyItemInfo.PolicyItem> getList(DefinedCode type);

}
