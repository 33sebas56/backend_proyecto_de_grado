package com.ucc.convenios.convenios.repository;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioVersionRepository extends JpaRepository<ConvenioVersion, UUID> {

    @EntityGraph(attributePaths = {"convenio", "createdBy"})
    List<ConvenioVersion> findByConvenioOrderByVersionNumberDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"convenio", "createdBy"})
    Optional<ConvenioVersion> findFirstByConvenioOrderByVersionNumberDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"convenio", "convenio.company", "createdBy"})
    Optional<ConvenioVersion> findWithDetailsById(UUID id);
}