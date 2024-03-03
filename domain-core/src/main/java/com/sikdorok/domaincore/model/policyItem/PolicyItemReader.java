package com.sikdorok.domaincore.model.policyItem;

import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PolicyItemReader {

    PolicyItem findByPolicyItemId(UUID policyItemId);

    List<PolicyItemInfo.PolicyItem> getList(DefinedCode type);

}
