package grupo12.practico.repositories;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.logging.Logger;

/**
 * Configuration service for Clinical Document API parameters.
 * Reads configuration from environment variables.
 */
@Singleton
@Startup
public class NodoDocumentosConfig {

    private static final Logger LOGGER = Logger.getLogger(NodoDocumentosConfig.class.getName());

    private String documentsApiBaseUrl;
    private String documentsApiKey;

    @PostConstruct
    public void init() {
        documentsApiBaseUrl = getEnvOrDefault("DOCUMENTS_API_BASE_URL",
                "http://host.docker.internal:8000/api");
        documentsApiKey = getEnvOrDefault("DOCUMENTS_API_KEY", "");

        LOGGER.info("Clinical Document API Configuration loaded:");
        LOGGER.info("  API Base URL: " + documentsApiBaseUrl);
        LOGGER.info("  API Key: " + (documentsApiKey.isEmpty() ? "NOT SET" : "***"));
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    public String getDocumentsApiBaseUrl() {
        return documentsApiBaseUrl;
    }

    public String getDocumentsApiKey() {
        return documentsApiKey;
    }

    public boolean isConfigured() {
        return documentsApiBaseUrl != null && !documentsApiBaseUrl.isEmpty()
                && documentsApiKey != null && !documentsApiKey.isEmpty();
    }
}