package com.ucc.convenios.notifications.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class AppLinkService {

    @Value("${app.system-url:http://localhost:4200}")
    private String systemUrl;

    @Value("${app.company-upload-path:/public/company-upload}")
    private String companyUploadPath;

    public String buildCompanyUploadUrl(String rawToken) {
        String encodedToken = encodePathSegment(rawToken);
        return join(systemUrl, companyUploadPath) + "/" + encodedToken;
    }

    public String buildSystemUrl() {
        return normalizeBaseUrl(systemUrl);
    }

    private String join(String baseUrl, String path) {
        String normalizedBaseUrl = normalizeBaseUrl(baseUrl);
        String normalizedPath = normalizePath(path);

        return normalizedBaseUrl + normalizedPath;
    }

    private String normalizeBaseUrl(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:4200";
        }

        String result = value.trim();
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    private String normalizePath(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String result = value.trim();

        if (!result.startsWith("/")) {
            result = "/" + result;
        }

        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    private String encodePathSegment(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return URLEncoder.encode(value.trim(), StandardCharsets.UTF_8);
    }
}