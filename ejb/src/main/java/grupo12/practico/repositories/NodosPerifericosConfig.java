package grupo12.practico.repositories;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.logging.Logger;

/**
 * Configuration service for Nodos Perifericos API parameters.
 * Reads configuration from environment variables.
 */
@Singleton
@Startup
public class NodosPerifericosConfig {

    private static final Logger LOGGER = Logger.getLogger(NodosPerifericosConfig.class.getName());

    private String nodoPerifericoApiBaseUrl;

    @PostConstruct
    public void init() {
        nodoPerifericoApiBaseUrl = getEnvOrDefault("NODO_PERIFERICO_API_BASE_URL",
                "");

        LOGGER.info("Nodos Perifericos Configuration loaded:");
        LOGGER.info("  API Base URL: " + nodoPerifericoApiBaseUrl);
    }

    private String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value != null ? value : defaultValue;
    }

    public String getNodoPerifericoApiBaseUrl() {
        return nodoPerifericoApiBaseUrl;
    }

    public String getClinicsApiUrl() {
        return nodoPerifericoApiBaseUrl + "clinics";
    }

    public boolean isConfigured() {
        return nodoPerifericoApiBaseUrl != null && !nodoPerifericoApiBaseUrl.isEmpty();
    }
}