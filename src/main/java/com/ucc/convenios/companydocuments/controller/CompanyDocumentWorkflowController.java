package com.ucc.convenios.companydocuments.controller;

import com.ucc.convenios.companydocuments.dto.CompanyDocumentCorrectionRequest;
import com.ucc.convenios.companydocuments.dto.CompanyDocumentDiscardRequest;
import com.ucc.convenios.companydocuments.dto.CompanyDocumentRequestResponse;
import com.ucc.convenios.companydocuments.dto.CompanyDocumentReviewRequest;
import com.ucc.convenios.companydocuments.dto.CompanySubmittedDocumentResponse;
import com.ucc.convenios.companydocuments.service.CompanyDocumentWorkflowService;
import com.ucc.convenios.shared.enums.CompanyExternalDocumentType;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenios/{convenioId}")
public class CompanyDocumentWorkflowController {

    private final CompanyDocumentWorkflowService companyDocumentWorkflowService;

    public CompanyDocumentWorkflowController(CompanyDocumentWorkflowService companyDocumentWorkflowService) {
        this.companyDocumentWorkflowService = companyDocumentWorkflowService;
    }

    @PostMapping("/request-company-documents")
    public CompanyDocumentRequestResponse requestCompanyDocuments(
            @PathVariable UUID convenioId,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.requestCompanyDocuments(convenioId, authentication);
    }

    @PostMapping(value = "/company-documents/admin-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public CompanySubmittedDocumentResponse adminUploadCompanyDocument(
            @PathVariable UUID convenioId,
            @RequestParam CompanyExternalDocumentType documentType,
            @RequestParam(required = false) String displayName,
            @RequestPart("file") MultipartFile file,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.adminUploadCompanyDocument(
                convenioId,
                documentType,
                displayName,
                file,
                authentication
        );
    }

    @GetMapping("/company-document-requests")
    public List<CompanyDocumentRequestResponse> findRequests(
            @PathVariable UUID convenioId,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.findRequests(convenioId, authentication);
    }

    @GetMapping("/company-documents")
    public List<CompanySubmittedDocumentResponse> findDocuments(
            @PathVariable UUID convenioId,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.findDocuments(convenioId, authentication);
    }

    @PostMapping("/company-documents/{documentId}/approve")
    public CompanySubmittedDocumentResponse approveDocument(
            @PathVariable UUID convenioId,
            @PathVariable UUID documentId,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.approveDocument(convenioId, documentId, authentication);
    }

    @PostMapping("/company-documents/{documentId}/observe")
    public CompanySubmittedDocumentResponse observeDocument(
            @PathVariable UUID convenioId,
            @PathVariable UUID documentId,
            @Valid @RequestBody CompanyDocumentReviewRequest request,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.observeDocument(convenioId, documentId, request, authentication);
    }

    @PostMapping("/company-documents/request-correction")
    public CompanyDocumentRequestResponse requestCorrection(
            @PathVariable UUID convenioId,
            @Valid @RequestBody CompanyDocumentCorrectionRequest request,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.requestCorrection(convenioId, request, authentication);
    }

    @PostMapping("/company-documents/discard")
    public CompanyDocumentRequestResponse discardEarlyDocumentProcess(
            @PathVariable UUID convenioId,
            @Valid @RequestBody CompanyDocumentDiscardRequest request,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.discardEarlyDocumentProcess(convenioId, request, authentication);
    }

    @PostMapping("/mark-documents-approved")
    public CompanyDocumentRequestResponse markDocumentsApproved(
            @PathVariable UUID convenioId,
            Authentication authentication
    ) {
        return companyDocumentWorkflowService.markDocumentsApproved(convenioId, authentication);
    }
}