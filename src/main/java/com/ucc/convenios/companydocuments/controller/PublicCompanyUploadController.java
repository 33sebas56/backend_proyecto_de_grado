package com.ucc.convenios.companydocuments.controller;

import com.ucc.convenios.companydocuments.dto.CompanySubmittedDocumentResponse;
import com.ucc.convenios.companydocuments.dto.PublicCompanyUploadInfoResponse;
import com.ucc.convenios.companydocuments.service.CompanyDocumentWorkflowService;
import com.ucc.convenios.shared.enums.CompanyExternalDocumentType;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/public/company-upload")
public class PublicCompanyUploadController {

    private static final long MAX_FILE_SIZE_BYTES = 25L * 1024L * 1024L;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png"
    );

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".pdf",
            ".jpg",
            ".jpeg",
            ".png"
    );

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
        validateUploadedFile(file);
        return companyDocumentWorkflowService.uploadCompanyDocument(token, documentType, displayName, file);
    }

    private void validateUploadedFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("El archivo es obligatorio");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BadRequestException("El archivo supera el tamaño máximo permitido de 25 MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BadRequestException("El archivo debe tener un nombre válido");
        }

        String normalizedFilename = originalFilename.trim().toLowerCase(Locale.ROOT);

        boolean hasAllowedExtension = ALLOWED_EXTENSIONS.stream()
                .anyMatch(normalizedFilename::endsWith);

        if (!hasAllowedExtension) {
            throw new BadRequestException("Formato de archivo no permitido. Solo se aceptan PDF, JPG, JPEG o PNG");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            throw new BadRequestException("No se pudo identificar el tipo del archivo");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BadRequestException("Tipo de archivo no permitido. Solo se aceptan PDF, JPG, JPEG o PNG");
        }
    }
}