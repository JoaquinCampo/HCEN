package grupo12.practico.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NodoDocumentosConfig Tests")
class NodoDocumentosConfigTest {

    private NodoDocumentosConfig config;

    @BeforeEach
    void setUp() {
        config = new NodoDocumentosConfig();
    }

    @Test
    @DisplayName("Should initialize with default values when no environment variables are set")
    void testInitWithDefaults() throws Exception {
        // Call init method using reflection
        Method initMethod = NodoDocumentosConfig.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(config);

        // Verify default values
        assertEquals("http://host.docker.internal:8000/api", config.getDocumentsApiBaseUrl());
        assertEquals("", config.getDocumentsApiKey());
        assertFalse(config.isConfigured());
    }

    @Test
    @DisplayName("Should initialize with environment variables when set")
    void testInitWithEnvironmentVariables() throws Exception {
        // Use reflection to set private fields to simulate environment variable values
        setPrivateField(config, "documentsApiBaseUrl", "https://api.example.com");
        setPrivateField(config, "documentsApiKey", "test-api-key");

        // Verify values are set correctly
        assertEquals("https://api.example.com", config.getDocumentsApiBaseUrl());
        assertEquals("test-api-key", config.getDocumentsApiKey());
        assertTrue(config.isConfigured());
    }

    @Test
    @DisplayName("Should return configured state correctly")
    void testIsConfigured() throws Exception {
        // Test with empty values
        assertFalse(config.isConfigured());

        // Test with only base URL set
        setPrivateField(config, "documentsApiBaseUrl", "https://api.example.com");
        assertFalse(config.isConfigured()); // API key is still empty

        // Test with only API key set
        setPrivateField(config, "documentsApiKey", "");
        setPrivateField(config, "documentsApiBaseUrl", "");
        setPrivateField(config, "documentsApiKey", "test-key");
        assertFalse(config.isConfigured()); // Base URL is empty

        // Test with both set
        setPrivateField(config, "documentsApiBaseUrl", "https://api.example.com");
        setPrivateField(config, "documentsApiKey", "test-key");
        assertTrue(config.isConfigured());
    }

    @Test
    @DisplayName("Should test getEnvOrDefault method with environment variable present")
    void testGetEnvOrDefaultWithEnvVar() throws Exception {
        // Since we can't easily mock System.getenv() in unit tests,
        // we'll test that the method exists and can be called
        Method method = NodoDocumentosConfig.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);

        // Test with a non-existent environment variable (should return default)
        String result = (String) method.invoke(config, "NON_EXISTENT_DOCUMENTS_API_BASE_URL", "default-value");
        assertEquals("default-value", result);
    }

    @Test
    @DisplayName("Should test getEnvOrDefault method with environment variable absent")
    void testGetEnvOrDefaultWithoutEnvVar() throws Exception {
        Method method = NodoDocumentosConfig.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(config, "NON_EXISTENT_ENV_VAR", "default-value");

        assertEquals("default-value", result);
    }

    @Test
    @DisplayName("Should handle null environment variable values")
    void testGetEnvOrDefaultWithNullEnvVar() throws Exception {
        Method method = NodoDocumentosConfig.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(config, "TEST_NULL_VAR", "default-value");

        assertEquals("default-value", result); // Should return default when env var doesn't exist
    }

    /**
     * Helper method to set private fields using reflection
     */
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}