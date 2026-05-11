package com.ucc.convenios.convenios.repository;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.users.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioRepository extends JpaRepository<Convenio, UUID> {

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    Optional<Convenio> findWithDetailsById(UUID id);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findByCurrentStatus(ConvenioStatus currentStatus);

    long countByCurrentStatus(ConvenioStatus currentStatus);

    long countByCreatedBy(User createdBy);

    long countByCreatedByAndCurrentStatus(User createdBy, ConvenioStatus currentStatus);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findByCreatedByOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}