package com.ucc.convenios.companydocuments.controller;

import com.ucc.convenios.companydocuments.dto.CompanySubmittedDocumentResponse;
import com.ucc.convenios.companydocuments.dto.PublicCompanyUploadInfoResponse;
import com.ucc.convenios.companydocuments.service.CompanyDocumentWorkflowService;
import com.ucc.convenios.shared.enums.CompanyExternalDocumentType;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public/company-upload")
public class PublicCompanyUploadController {

    private final CompanyDocumentWorkflowService companyDocumentWorkflowService;

    public PublicCompanyUploadController(CompanyDocumentWorkflowService companyDocumentWorkflowService) {
        this.companyDocumentWorkflowService = companyDocumentWorkflowService;
    }

    @GetMapping("/{token}")
    public PublicCompanyUploadInfoResponse getUploadInfo(@PathVariable String token) {
        return companyDocumentWorkflowService.getPublicUploadInfo(token);
    }

    @PostMapping(value = "/{token}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompanySubmittedDocumentResponse uploadDocument(
            @PathVariable String token,
            @RequestParam CompanyExternalDocumentType documentType,
            @RequestParam(required = false) String displayName,
            @RequestPart("file") MultipartFile file
    ) {
        return companyDocumentWorkflowService.uploadCompanyDocument(token, documentType, displayName, file);
    }
}