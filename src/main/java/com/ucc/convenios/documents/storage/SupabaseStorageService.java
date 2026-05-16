package com.ucc.convenios.documents.storage;

import com.ucc.convenios.shared.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "app.storage.provider", havingValue = "supabase")
public class SupabaseStorageService implements FileStorageService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final HttpClient httpClient;
    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String bucket;

    public SupabaseStorageService(
            @Value("${app.supabase.url:}") String supabaseUrl,
            @Value("${app.supabase.service-role-key:}") String serviceRoleKey,
            @Value("${app.supabase.bucket:convenios-documents}") String bucket
    ) {
        this.supabaseUrl = normalizeBaseUrl(supabaseUrl);
        this.serviceRoleKey = serviceRoleKey;
        this.bucket = bucket;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    @Override
    public String getPreviewPdfStoragePath(UUID convenioId) {
        return "convenios/previews/" + convenioId + "/preview.pdf";
    }

    @Override
    public String savePreviewPdf(UUID convenioId, byte[] fileBytes) {
        String objectPath = getPreviewPdfStoragePath(convenioId);
        upload(objectPath, PDF_CONTENT_TYPE, fileBytes);
        return objectPath;
    }

    @Override
    public String saveOfficialPdf(UUID convenioId, String fileName, byte[] fileBytes) {
        String safeFileName = sanitizeFilename(fileName);
        String objectPath = "convenios/official/" + convenioId + "/" + safeFileName;
        upload(objectPath, PDF_CONTENT_TYPE, fileBytes);
        return objectPath;
    }

    @Override
    public String saveOfficialPdf(UUID convenioId, Integer versionNumber, byte[] fileBytes) {
        String objectPath = "convenios/official/" + convenioId + "/version-" + versionNumber + ".pdf";
        upload(objectPath, PDF_CONTENT_TYPE, fileBytes);
        return objectPath;
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

        String objectPath = "convenios/company-documents/"
                + convenioId + "/"
                + requestId + "/"
                + storedFilename;

        upload(objectPath, resolveContentType(contentType), fileBytes);
        return objectPath;
    }

    @Override
    public byte[] readFile(String storagePath) {
        validateConfiguration();

        if (storagePath == null || storagePath.isBlank()) {
            throw new BadRequestException("Ruta de almacenamiento inválida");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(objectUri(storagePath))
                .timeout(Duration.ofSeconds(60))
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .GET()
                .build();

        try {
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (!isSuccess(response.statusCode())) {
                throw new BadRequestException("No se pudo leer el archivo desde Supabase Storage. Código: " + response.statusCode());
            }

            return response.body();
        } catch (IOException exception) {
            throw new BadRequestException("Error de conexión leyendo archivo desde Supabase Storage");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Lectura de archivo interrumpida");
        }
    }

    @Override
    public boolean exists(String storagePath) {
        validateConfiguration();

        if (storagePath == null || storagePath.isBlank()) {
            return false;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(objectUri(storagePath))
                .timeout(Duration.ofSeconds(30))
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .GET()
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return isSuccess(response.statusCode());
        } catch (IOException exception) {
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void deleteFileIfExists(String storagePath) {
        validateConfiguration();

        if (storagePath == null || storagePath.isBlank()) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(objectUri(storagePath))
                .timeout(Duration.ofSeconds(60))
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return;
            }

            if (!isSuccess(response.statusCode())) {
                throw new BadRequestException("No se pudo eliminar el archivo en Supabase Storage. Código: "
                        + response.statusCode() + " Respuesta: " + response.body());
            }
        } catch (IOException exception) {
            throw new BadRequestException("Error de conexión eliminando archivo en Supabase Storage");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Eliminación de archivo interrumpida");
        }
    }

    private void upload(String objectPath, String contentType, byte[] fileBytes) {
        validateConfiguration();

        if (fileBytes == null || fileBytes.length == 0) {
            throw new BadRequestException("El archivo no puede estar vacío");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(objectUri(objectPath))
                .timeout(Duration.ofSeconds(120))
                .header("apikey", serviceRoleKey)
                .header("Authorization", "Bearer " + serviceRoleKey)
                .header("Content-Type", resolveContentType(contentType))
                .header("x-upsert", "true")
                .POST(HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (!isSuccess(response.statusCode())) {
                throw new BadRequestException("No se pudo subir el archivo a Supabase Storage. Código: "
                        + response.statusCode() + " Respuesta: " + response.body());
            }
        } catch (IOException exception) {
            throw new BadRequestException("Error de conexión subiendo archivo a Supabase Storage");
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BadRequestException("Subida de archivo interrumpida");
        }
    }

    private URI objectUri(String objectPath) {
        return URI.create(supabaseUrl + "/storage/v1/object/" + encodePath(bucket) + "/" + encodePath(objectPath));
    }


    private String encodePath(String path) {
        return Arrays.stream(path.split("/"))
                .map(segment -> URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }

    private boolean isSuccess(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    private String resolveContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return DEFAULT_CONTENT_TYPE;
        }

        return contentType.trim();
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "documento";
        }

        return filename
                .trim()
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String normalizeBaseUrl(String rawUrl) {
        if (rawUrl == null) {
            return "";
        }

        String normalized = rawUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private void validateConfiguration() {
        if (supabaseUrl == null || supabaseUrl.isBlank()) {
            throw new BadRequestException("Falta configurar SUPABASE_URL");
        }

        if (serviceRoleKey == null || serviceRoleKey.isBlank()) {
            throw new BadRequestException("Falta configurar SUPABASE_SERVICE_ROLE_KEY");
        }

        if (bucket == null || bucket.isBlank()) {
            throw new BadRequestException("Falta configurar SUPABASE_BUCKET");
        }
    }
}


