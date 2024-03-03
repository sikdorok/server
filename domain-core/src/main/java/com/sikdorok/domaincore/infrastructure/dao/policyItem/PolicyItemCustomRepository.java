package com.sikdorok.domaincore.infrastructure.dao.policyItem;

import com.sikdorok.domaincore.model.policyItem.PolicyItemInfo;
import com.sikdorok.domaincore.model.shared.DefinedCode;

import java.util.List;

public interface PolicyItemCustomRepository {

    List<PolicyItemInfo.PolicyItem> getList(DefinedCode type);

}
