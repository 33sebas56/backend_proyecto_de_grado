package com.ucc.convenios.convenios.repository;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConvenioStatusHistoryRepository extends JpaRepository<ConvenioStatusHistory, UUID> {

    List<ConvenioStatusHistory> findByConvenioOrderByPerformedAtDesc(Convenio convenio);
}