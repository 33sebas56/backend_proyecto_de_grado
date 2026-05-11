package com.ucc.convenios.companies.repository;

import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.shared.enums.CompanyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {

    Optional<Company> findByNit(String nit);

    boolean existsByNit(String nit);

    List<Company> findByStatus(CompanyStatus status);

    long countByStatus(CompanyStatus status);
}