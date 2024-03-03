package com.sikdorok.domaincore.model.photos;

import com.sikdorok.domaincore.model.shared.DefinedCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhotosJpaRepository extends JpaRepository<Photos, UUID> {

    Optional<Photos> findByPhotosId(UUID photosId);

    Optional<Photos> findByToken(UUID photosToken);

    List<Photos> findByTypeAndSubType(DefinedCode type, DefinedCode subType);

}
