package com.ucc.convenios.companies.repository;

import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.companies.entity.CompanyDocument;
import com.ucc.convenios.shared.enums.CompanyDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyDocumentRepository extends JpaRepository<CompanyDocument, UUID> {

    List<CompanyDocument> findByCompany(Company company);

    Optional<CompanyDocument> findByCompanyAndDocumentType(
            Company company,
            CompanyDocumentType documentType
    );
}