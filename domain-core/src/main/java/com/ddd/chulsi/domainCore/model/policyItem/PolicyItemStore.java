package com.ddd.chulsi.domainCore.model.policyItem;

public interface PolicyItemStore {

    PolicyItem register(PolicyItem policyItem);

    void delete(PolicyItem policyItem);

}
