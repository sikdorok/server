package com.ddd.chulsi.domainCore.infrastructure.dao.policyItem;

import com.ddd.chulsi.domainCore.model.policyItem.PolicyItemInfo;
import com.ddd.chulsi.domainCore.model.shared.DefinedCode;

import java.util.List;

public interface PolicyItemCustomRepository {

    List<PolicyItemInfo.PolicyItem> getList(DefinedCode type);

}
