package com.ddd.chulsi.domainCore.model.appVersion;

import com.ddd.chulsi.domainCore.model.shared.DefinedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppVersionJpaRepository extends JpaRepository<AppVersion, UUID> {

    Optional<AppVersion> findByTypeAndMajorAndMinorAndPatch(DefinedCode type, int major, int minor, int patch);

    Optional<AppVersion> findFirstByTypeOrderByMajorDescMinorDescPatchDesc(DefinedCode type);

}
