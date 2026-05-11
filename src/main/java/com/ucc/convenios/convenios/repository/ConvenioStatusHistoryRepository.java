package com.ucc.convenios.convenios.repository;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConvenioStatusHistoryRepository extends JpaRepository<ConvenioStatusHistory, UUID> {

    @EntityGraph(attributePaths = {"convenio", "convenio.company", "performedBy"})
    List<ConvenioStatusHistory> findByConvenioOrderByPerformedAtDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"convenio", "convenio.company", "performedBy"})
    List<ConvenioStatusHistory> findAllByOrderByPerformedAtDesc(Pageable pageable);
}