package com.ucc.convenios.convenios.repository;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioRepository extends JpaRepository<Convenio, UUID> {

    boolean existsByCode(String code);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    Optional<Convenio> findWithDetailsById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    @Query("select c from Convenio c where c.id = :id")
    Optional<Convenio> findForUpdateById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findByCurrentStatus(ConvenioStatus currentStatus);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findByCurrentStatusOrderByUpdatedAtDesc(ConvenioStatus currentStatus, Pageable pageable);

    long countByCurrentStatus(ConvenioStatus currentStatus);

    long countByCreatedBy(User createdBy);

    long countByCreatedByAndCurrentStatus(User createdBy, ConvenioStatus currentStatus);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findByCreatedByOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    @EntityGraph(attributePaths = {"company", "createdBy", "currentVersion"})
    List<Convenio> findAllByOrderByUpdatedAtDesc(Pageable pageable);
}