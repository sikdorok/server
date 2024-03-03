package com.sikdorok.domaincore.model.policyItem;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PolicyItemService {

    PolicyItem register(PolicyItem policyItem);

    PolicyItem findByPolicyItemId(UUID policyItemId);

    void delete(PolicyItem policyItem);

    List<PolicyItemInfo.PolicyItem> getList(DefinedCode type);

}
