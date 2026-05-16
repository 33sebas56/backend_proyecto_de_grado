package com.ucc.convenios.documents.storage;

import java.util.UUID;

public interface FileStorageService {

    String getPreviewPdfStoragePath(UUID convenioId);

    String savePreviewPdf(UUID convenioId, byte[] fileBytes);

    String saveOfficialPdf(UUID convenioId, String fileName, byte[] fileBytes);

    String saveOfficialPdf(UUID convenioId, Integer versionNumber, byte[] fileBytes);

    String saveCompanySubmittedDocument(
            UUID convenioId,
            UUID requestId,
            String originalFilename,
            String contentType,
            byte[] fileBytes
    );

    byte[] readFile(String storagePath);

    boolean exists(String storagePath);

    void deleteFileIfExists(String storagePath);
}