package com.ucc.convenios.convenios.controller;

import com.ucc.convenios.convenios.dto.ConvenioResponse;
import com.ucc.convenios.convenios.dto.ConvenioStatusHistoryResponse;
import com.ucc.convenios.convenios.dto.ConvenioVersionResponse;
import com.ucc.convenios.convenios.dto.CreateConvenioRequest;
import com.ucc.convenios.convenios.service.ConvenioService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.ucc.convenios.convenios.dto.ConvenioGeneratedDocumentResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/convenios")
public class ConvenioController {

    private final ConvenioService convenioService;

    public ConvenioController(ConvenioService convenioService) {
        this.convenioService = convenioService;
    }

    @PostMapping
    public ConvenioResponse createConvenio(
            @Valid @RequestBody CreateConvenioRequest request,
            Authentication authentication
    ) {
        return convenioService.createConvenio(request, authentication);
    }

    @GetMapping
    public List<ConvenioResponse> findAll() {
        return convenioService.findAll();
    }

    @GetMapping("/{id}")
    public ConvenioResponse findById(@PathVariable UUID id) {
        return convenioService.findById(id);
    }

    @GetMapping("/{id}/versions")
    public List<ConvenioVersionResponse> findVersions(@PathVariable UUID id) {
        return convenioService.findVersions(id);
    }

    @GetMapping("/{id}/history")
    public List<ConvenioStatusHistoryResponse> findStatusHistory(@PathVariable UUID id) {
        return convenioService.findStatusHistory(id);
    }

    @PostMapping("/{id}/submit")
    public ConvenioResponse submitConvenio(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return convenioService.submitConvenio(id, authentication);
    }
    @PostMapping("/{id}/preview-pdf")
    public String generatePreviewPdf(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        return convenioService.generatePreviewPdf(id, authentication);
    }

    @GetMapping("/{id}/preview-pdf")
    public ResponseEntity<byte[]> getPreviewPdf(@PathVariable UUID id) {
        byte[] pdfBytes = convenioService.getPreviewPdf(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=preview-convenio.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{convenioId}/versions/{versionId}/pdf")
    public ResponseEntity<byte[]> getOfficialPdf(
            @PathVariable UUID convenioId,
            @PathVariable UUID versionId
    ) {
        byte[] pdfBytes = convenioService.getOfficialPdf(convenioId, versionId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=convenio.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
    @GetMapping("/{id}/documents")
    public List<ConvenioGeneratedDocumentResponse> findGeneratedDocuments(@PathVariable UUID id) {
        return convenioService.findGeneratedDocuments(id);
    }

    @GetMapping("/{convenioId}/documents/{documentId}/pdf")
    public ResponseEntity<byte[]> getGeneratedDocumentPdf(
            @PathVariable UUID convenioId,
            @PathVariable UUID documentId
    ) {
        byte[] pdfBytes = convenioService.getGeneratedDocumentPdf(convenioId, documentId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=convenio-documento.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}