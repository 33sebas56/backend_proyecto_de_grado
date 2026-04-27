package com.ucc.convenios.companies.repository;

import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.companies.entity.CompanyValidationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompanyValidationHistoryRepository extends JpaRepository<CompanyValidationHistory, UUID> {

    List<CompanyValidationHistory> findByCompanyOrderByPerformedAtDesc(Company company);
}