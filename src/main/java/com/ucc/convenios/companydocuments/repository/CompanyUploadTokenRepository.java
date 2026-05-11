package com.ucc.convenios.companydocuments.repository;

import com.ucc.convenios.companydocuments.entity.CompanyDocumentRequest;
import com.ucc.convenios.companydocuments.entity.CompanyUploadToken;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyUploadTokenRepository extends JpaRepository<CompanyUploadToken, UUID> {

    @EntityGraph(attributePaths = {"request", "convenio", "company", "createdBy"})
    Optional<CompanyUploadToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);

    List<CompanyUploadToken> findByRequestAndRevokedAtIsNull(CompanyDocumentRequest request);
}