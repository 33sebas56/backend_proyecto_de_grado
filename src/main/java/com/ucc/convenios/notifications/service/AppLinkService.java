package com.ucc.convenios.notifications.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppLinkService {

    @Value("${app.public-base-url:http://localhost:8080}")
    private String publicBaseUrl;

    @Value("${app.system-url:http://localhost:8080}")
    private String systemUrl;

    @Value("${app.company-upload-path:/api/public/company-upload}")
    private String companyUploadPath;

    public String buildCompanyUploadUrl(String rawToken) {
        return join(publicBaseUrl, companyUploadPath) + "/" + rawToken;
    }

    public String buildSystemUrl() {
        return systemUrl;
    }

    private String join(String baseUrl, String path) {
        String normalizedBaseUrl = normalizeBaseUrl(baseUrl);
        String normalizedPath = normalizePath(path);

        return normalizedBaseUrl + normalizedPath;
    }

    private String normalizeBaseUrl(String value) {
        if (value == null || value.isBlank()) {
            return "http://localhost:8080";
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
}