package com.ucc.convenios.companies.service;

import com.ucc.convenios.companies.dto.CompanyResponse;
import com.ucc.convenios.companies.dto.CompanyValidationHistoryResponse;
import com.ucc.convenios.companies.dto.CompanyValidationRequest;
import com.ucc.convenios.companies.dto.CreateCompanyRequest;
import com.ucc.convenios.companies.entity.Company;
import com.ucc.convenios.companies.entity.CompanyValidationHistory;
import com.ucc.convenios.companies.repository.CompanyRepository;
import com.ucc.convenios.companies.repository.CompanyValidationHistoryRepository;
import com.ucc.convenios.shared.enums.CompanyStatus;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyValidationHistoryRepository companyValidationHistoryRepository;
    private final UserRepository userRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            CompanyValidationHistoryRepository companyValidationHistoryRepository,
            UserRepository userRepository
    ) {
        this.companyRepository = companyRepository;
        this.companyValidationHistoryRepository = companyValidationHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        String normalizedNit = normalizeNit(request.getNit());
        String normalizedContactEmail = normalizeEmail(request.getContactEmail());

        if (companyRepository.existsByNit(normalizedNit)) {
            throw new BadRequestException("Ya existe una empresa registrada con este NIT");
        }

        Company company = new Company();
        company.setNit(normalizedNit);
        company.setBusinessName(request.getBusinessName());
        company.setTradeName(request.getTradeName());
        company.setIdentificationType(
                request.getIdentificationType() == null || request.getIdentificationType().isBlank()
                        ? "NIT"
                        : request.getIdentificationType()
        );
        company.setLegalRepresentativeName(request.getLegalRepresentativeName());
        company.setContactEmail(normalizedContactEmail);
        company.setContactPhone(request.getContactPhone());
        company.setAddress(request.getAddress());
        company.setStatus(CompanyStatus.BORRADOR);
        company.setCreatedBy(currentUser);

        Company savedCompany = companyRepository.save(company);

        registerHistory(
                savedCompany,
                null,
                CompanyStatus.BORRADOR,
                "Empresa creada en estado borrador",
                currentUser
        );

        return CompanyResponse.fromEntity(savedCompany);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(CompanyResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        Company company = getCompanyById(id);
        return CompanyResponse.fromEntity(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponse findByNit(String nit) {
        Company company = companyRepository.findByNit(normalizeNit(nit))
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));

        return CompanyResponse.fromEntity(company);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> findPendingValidation() {
        return companyRepository.findByStatus(CompanyStatus.PENDIENTE_VALIDACION)
                .stream()
                .map(CompanyResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CompanyResponse submitForValidation(UUID companyId, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Company company = getCompanyById(companyId);

        if (company.getStatus() != CompanyStatus.BORRADOR &&
                company.getStatus() != CompanyStatus.OBSERVADA) {
            throw new BadRequestException("La empresa no se puede enviar a validación desde su estado actual");
        }

        if (company.getContactEmail() == null || company.getContactEmail().isBlank()) {
            throw new BadRequestException("La empresa debe tener correo de contacto antes de enviarse a validación");
        }

        CompanyStatus previousStatus = company.getStatus();

        company.setStatus(CompanyStatus.PENDIENTE_VALIDACION);

        Company savedCompany = companyRepository.save(company);

        registerHistory(
                savedCompany,
                previousStatus,
                CompanyStatus.PENDIENTE_VALIDACION,
                "Empresa enviada a validación jurídica",
                currentUser
        );

        return CompanyResponse.fromEntity(savedCompany);
    }

    @Transactional
    public CompanyResponse validateCompany(
            UUID companyId,
            CompanyValidationRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Company company = getCompanyById(companyId);

        if (company.getStatus() != CompanyStatus.PENDIENTE_VALIDACION) {
            throw new BadRequestException("Solo se pueden validar empresas pendientes de validación");
        }

        if (company.getContactEmail() == null || company.getContactEmail().isBlank()) {
            throw new BadRequestException("La empresa no puede validarse sin correo de contacto");
        }

        CompanyStatus previousStatus = company.getStatus();

        company.setStatus(CompanyStatus.VALIDADA);
        company.setValidatedBy(currentUser);
        company.setValidatedAt(LocalDateTime.now());

        Company savedCompany = companyRepository.save(company);

        registerHistory(
                savedCompany,
                previousStatus,
                CompanyStatus.VALIDADA,
                normalizeComment(request.getComment(), "Empresa validada jurídicamente"),
                currentUser
        );

        return CompanyResponse.fromEntity(savedCompany);
    }

    @Transactional
    public CompanyResponse observeCompany(
            UUID companyId,
            CompanyValidationRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Company company = getCompanyById(companyId);

        if (company.getStatus() != CompanyStatus.PENDIENTE_VALIDACION) {
            throw new BadRequestException("Solo se pueden observar empresas pendientes de validación");
        }

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("El comentario es obligatorio para observar una empresa");
        }

        CompanyStatus previousStatus = company.getStatus();

        company.setStatus(CompanyStatus.OBSERVADA);

        Company savedCompany = companyRepository.save(company);

        registerHistory(
                savedCompany,
                previousStatus,
                CompanyStatus.OBSERVADA,
                request.getComment(),
                currentUser
        );

        return CompanyResponse.fromEntity(savedCompany);
    }

    @Transactional
    public CompanyResponse rejectCompany(
            UUID companyId,
            CompanyValidationRequest request,
            Authentication authentication
    ) {
        User currentUser = getCurrentUser(authentication);
        Company company = getCompanyById(companyId);

        if (company.getStatus() != CompanyStatus.PENDIENTE_VALIDACION) {
            throw new BadRequestException("Solo se pueden rechazar empresas pendientes de validación");
        }

        if (request.getComment() == null || request.getComment().isBlank()) {
            throw new BadRequestException("El comentario es obligatorio para rechazar una empresa");
        }

        CompanyStatus previousStatus = company.getStatus();

        company.setStatus(CompanyStatus.RECHAZADA);

        Company savedCompany = companyRepository.save(company);

        registerHistory(
                savedCompany,
                previousStatus,
                CompanyStatus.RECHAZADA,
                request.getComment(),
                currentUser
        );

        return CompanyResponse.fromEntity(savedCompany);
    }

    @Transactional(readOnly = true)
    public List<CompanyValidationHistoryResponse> findValidationHistory(UUID companyId) {
        Company company = getCompanyById(companyId);

        return companyValidationHistoryRepository.findByCompanyOrderByPerformedAtDesc(company)
                .stream()
                .map(CompanyValidationHistoryResponse::fromEntity)
                .toList();
    }

    private void registerHistory(
            Company company,
            CompanyStatus previousStatus,
            CompanyStatus newStatus,
            String comment,
            User performedBy
    ) {
        CompanyValidationHistory history = new CompanyValidationHistory();
        history.setCompany(company);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setComment(comment);
        history.setPerformedBy(performedBy);

        companyValidationHistoryRepository.save(history);
    }

    private Company getCompanyById(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
    }

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
    }

    private String normalizeNit(String nit) {
        return nit.trim().toUpperCase();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("El correo de contacto de la empresa es obligatorio");
        }

        return email.trim().toLowerCase();
    }

    private String normalizeComment(String comment, String defaultComment) {
        if (comment == null || comment.isBlank()) {
            return defaultComment;
        }

        return comment.trim();
    }
}