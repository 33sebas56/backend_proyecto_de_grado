package com.ucc.convenios.companies.controller;

import com.ucc.convenios.companies.dto.CompanyResponse;
import com.ucc.convenios.companies.dto.CompanyValidationHistoryResponse;
import com.ucc.convenios.companies.dto.CompanyValidationRequest;
import com.ucc.convenios.companies.dto.CreateCompanyRequest;
import com.ucc.convenios.companies.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public CompanyResponse createCompany(
            @Valid @RequestBody CreateCompanyRequest request,
            Authentication authentication
    ) {
        return companyService.createCompany(request, authentication);
    }

    @GetMapping
    public List<CompanyResponse> findAll() {
        return companyService.findAll();
    }

    @GetMapping("/{id}")
    public CompanyResponse findById(@PathVariable UUID id) {
        return companyService.findById(id);
    }

    @GetMapping("/by-nit/{nit}")
    public CompanyResponse findByNit(@PathVariable String nit) {
        return companyService.findByNit(nit);
    }

    @GetMapping("/pending-validation")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REVISOR_JURIDICO')")
    public List<CompanyResponse> findPendingValidation() {
        return companyService.findPendingValidation();
    }

    @PostMapping("/{id}/submit-validation")
    public CompanyResponse submitForValidation(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return companyService.submitForValidation(id, authentication);
    }

    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REVISOR_JURIDICO')")
    public CompanyResponse validateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyValidationRequest request,
            Authentication authentication
    ) {
        return companyService.validateCompany(id, request, authentication);
    }

    @PostMapping("/{id}/observe")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REVISOR_JURIDICO')")
    public CompanyResponse observeCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyValidationRequest request,
            Authentication authentication
    ) {
        return companyService.observeCompany(id, request, authentication);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('REVISOR_JURIDICO')")
    public CompanyResponse rejectCompany(
            @PathVariable UUID id,
            @Valid @RequestBody CompanyValidationRequest request,
            Authentication authentication
    ) {
        return companyService.rejectCompany(id, request, authentication);
    }

    @GetMapping("/{id}/history")
    public List<CompanyValidationHistoryResponse> findValidationHistory(@PathVariable UUID id) {
        return companyService.findValidationHistory(id);
    }
}