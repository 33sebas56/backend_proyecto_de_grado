package com.ucc.convenios.companydocuments.repository;

import com.ucc.convenios.companydocuments.entity.CompanyDocumentRequest;
import com.ucc.convenios.convenios.entity.Convenio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyDocumentRequestRepository extends JpaRepository<CompanyDocumentRequest, UUID> {

    @EntityGraph(attributePaths = {"convenio", "company", "reviewedBy"})
    List<CompanyDocumentRequest> findByConvenioOrderByRoundNumberDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"convenio", "company", "reviewedBy"})
    Optional<CompanyDocumentRequest> findTopByConvenioOrderByRoundNumberDesc(Convenio convenio);
}