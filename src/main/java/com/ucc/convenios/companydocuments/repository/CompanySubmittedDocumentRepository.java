package com.ucc.convenios.companydocuments.repository;

import com.ucc.convenios.companydocuments.entity.CompanyDocumentRequest;
import com.ucc.convenios.companydocuments.entity.CompanySubmittedDocument;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.CompanyExternalDocumentType;
import com.ucc.convenios.shared.enums.CompanySubmittedDocumentStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanySubmittedDocumentRepository extends JpaRepository<CompanySubmittedDocument, UUID> {

    @EntityGraph(attributePaths = {"request", "convenio", "convenio.company", "reviewedBy", "replacedByDocument"})
    List<CompanySubmittedDocument> findByRequestOrderByUploadedAtDesc(CompanyDocumentRequest request);

    @EntityGraph(attributePaths = {"request", "convenio", "convenio.company", "reviewedBy", "replacedByDocument"})
    List<CompanySubmittedDocument> findByConvenioOrderByUploadedAtDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"request", "convenio", "convenio.company", "reviewedBy", "replacedByDocument"})
    Optional<CompanySubmittedDocument> findTopByConvenioAndDocumentTypeAndStatusInOrderByUploadedAtDesc(
            Convenio convenio,
            CompanyExternalDocumentType documentType,
            List<CompanySubmittedDocumentStatus> statuses
    );

    @EntityGraph(attributePaths = {"request", "convenio", "convenio.company", "reviewedBy", "replacedByDocument"})
    List<CompanySubmittedDocument> findAllByOrderByUploadedAtDesc(Pageable pageable);
}