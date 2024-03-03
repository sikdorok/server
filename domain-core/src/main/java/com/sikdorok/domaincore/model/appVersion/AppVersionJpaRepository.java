package com.sikdorok.domaincore.model.appVersion;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppVersionJpaRepository extends JpaRepository<AppVersion, UUID> {

    Optional<AppVersion> findByTypeAndMajorAndMinorAndPatch(DefinedCode type, int major, int minor, int patch);

    Optional<AppVersion> findFirstByTypeOrderByMajorDescMinorDescPatchDesc(DefinedCode type);

}
