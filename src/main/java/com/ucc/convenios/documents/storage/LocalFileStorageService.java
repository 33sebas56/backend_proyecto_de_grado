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

    public byte[] readFile(String storagePath) {
        try {
            return Files.readAllBytes(Path.of(storagePath));
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo leer el archivo PDF");
        }
    }

    public boolean exists(String storagePath) {
        return Files.exists(Path.of(storagePath));
    }

    private Path writeFile(Path directory, Path filePath, byte[] fileBytes) {
        try {
            Files.createDirectories(directory);
            Files.write(filePath, fileBytes);
            return filePath;
        } catch (IOException exception) {
            throw new BadRequestException("No se pudo guardar el archivo PDF");
        }
    }
}