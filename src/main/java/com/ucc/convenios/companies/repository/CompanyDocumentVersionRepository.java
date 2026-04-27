package com.ucc.convenios.companies.repository;

import com.ucc.convenios.companies.entity.CompanyDocument;
import com.ucc.convenios.companies.entity.CompanyDocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyDocumentVersionRepository extends JpaRepository<CompanyDocumentVersion, UUID> {

    List<CompanyDocumentVersion> findByCompanyDocumentOrderByVersionNumberDesc(
            CompanyDocument companyDocument
    );
}