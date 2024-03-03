package com.sikdorok.domaincore.model.policyItem;

public interface PolicyItemStore {

    PolicyItem register(PolicyItem policyItem);

    void delete(PolicyItem policyItem);

}
