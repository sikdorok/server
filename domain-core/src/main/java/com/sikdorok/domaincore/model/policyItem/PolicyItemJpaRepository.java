package com.sikdorok.domaincore.model.policyItem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PolicyItemJpaRepository extends JpaRepository<PolicyItem, UUID> {
}
