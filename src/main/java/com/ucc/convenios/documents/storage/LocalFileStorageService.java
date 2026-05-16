package com.ucc.convenios.documents.storage;

import com.ucc.convenios.shared.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${app.storage.base-path:storage}")
    private String basePath;

    @Override
    public String getPreviewPdfStoragePath(UUID convenioId) {
        return Path.of(basePath, "convenios", "previews", convenioId.toString(), "preview.pdf").toString();
    }

    @Override
    public String savePreviewPdf(UUID convenioId, byte[] fileBytes) {
        Path directory = Path.of(basePath, "convenios", "previews", convenioId.toString());
        Path filePath = directory.resolve("preview.pdf");

        return writeFile(directory, filePath, fileBytes).toString();
    }

    @Override
    public String saveOfficialPdf(UUID convenioId, String fileName, byte[] fileBytes) {
        Path directory = Path.of(basePath, "convenios", "official", convenioId.toString());
        Path filePath = directory.resolve(fileName);

        return writeFile(directory, filePath, fileBytes).toString();
    }

    @Override
    public String saveOfficialPdf(UUID convenioId, Integer versionNumber, byte[] fileBytes) {
        Path directory = Path.of(basePath, "convenios", "official", convenioId.toString());
        Path filePath = directory.resolve("version-" + versionNumber + ".pdf");

        return writeFile(directory, filePath, fileBytes).toString();
    }

    @Override
    public String saveCompanySubmittedDocument(
            UUID convenioId,
            UUID requestId,
            String originalFilename,
            String contentType,
            byte[] fileBytes
    ) {
        String safeOriginalFilename = sanitizeFilename(originalFilename);
        String storedFilename = UUID.randomUUID() + "-" + safeOriginalFilename;

        Path directory = Path.of(
                basePath,
                "convenios",
                "company-documents",
                convenioId.toString(),
                requestId.toString()
        );

        Path filePath = directory.resolve(storedFilename);
        return writeFile(directory, filePath, fileBytes).toString();
    }

    @Override
    public byte[] readFile(String storagePath) {
        try {
            return Files.readAllBytes(Path.of(storagePath));
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo leer el archivo");
        }
    }

    @Override
    public boolean exists(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return false;
        }

        return Files.exists(Path.of(storagePath));
    }

    @Override
    public void deleteFileIfExists(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(Path.of(storagePath));
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo eliminar el archivo físico");
        }
    }

    private Path writeFile(Path directory, Path filePath, byte[] fileBytes) {
        try {
            Files.createDirectories(directory);
            Files.write(filePath, fileBytes);
            return filePath;
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo guardar el archivo");
        }
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "documento";
        }

        return filename
                .trim()
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}