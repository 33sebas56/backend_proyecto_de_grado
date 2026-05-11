package com.ucc.convenios.documents.storage;

import com.ucc.convenios.shared.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class LocalFileStorageService {

    @Value("${app.storage.base-path:storage}")
    private String basePath;

    public Path savePreviewPdf(UUID convenioId, byte[] fileBytes) {
        Path directory = Path.of(basePath, "convenios", "previews", convenioId.toString());
        Path filePath = directory.resolve("preview.pdf");

        return writeFile(directory, filePath, fileBytes);
    }

    public Path saveOfficialPdf(UUID convenioId, String fileName, byte[] fileBytes) {
        Path directory = Path.of(basePath, "convenios", "official", convenioId.toString());
        Path filePath = directory.resolve(fileName);

        return writeFile(directory, filePath, fileBytes);
    }

    public Path saveOfficialPdf(UUID convenioId, Integer versionNumber, byte[] fileBytes) {
        Path directory = Path.of(basePath, "convenios", "official", convenioId.toString());
        Path filePath = directory.resolve("version-" + versionNumber + ".pdf");

        return writeFile(directory, filePath, fileBytes);
    }

    public Path saveCompanySubmittedDocument(
            UUID convenioId,
            UUID requestId,
            String originalFilename,
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
        return writeFile(directory, filePath, fileBytes);
    }

    public byte[] readFile(String storagePath) {
        try {
            return Files.readAllBytes(Path.of(storagePath));
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo leer el archivo");
        }
    }

    public boolean exists(String storagePath) {
        return Files.exists(Path.of(storagePath));
    }

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