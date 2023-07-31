package com.ddd.chulsi.domainCore.model.policyItem;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.util.List;
import java.util.UUID;

public interface PolicyItemReader {

    PolicyItem findByPolicyItemId(UUID policyItemId);

    List<PolicyItemInfo.PolicyItem> getList(DefinedCode type);

}
