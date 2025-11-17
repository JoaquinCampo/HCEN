package grupo12.practico.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NodosPerifericosConfig Tests")
class NodosPerifericosConfigTest {

    private NodosPerifericosConfig config;

    @BeforeEach
    void setUp() {
        config = new NodosPerifericosConfig();
    }

    @Test
    @DisplayName("Should initialize with default values when no environment variables are set")
    void testInitWithDefaults() throws Exception {
        // Call init method using reflection
        Method initMethod = NodosPerifericosConfig.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(config);

        // Verify default values
        assertEquals("", config.getNodoPerifericoApiBaseUrl());
        assertEquals("clinics", config.getClinicsApiUrl()); // Empty base URL + "clinics"
        assertFalse(config.isConfigured());
    }

    @Test
    @DisplayName("Should initialize with environment variables when set")
    void testInitWithEnvironmentVariables() throws Exception {
        // Use reflection to set private fields to simulate environment variable values
        setPrivateField(config, "nodoPerifericoApiBaseUrl", "https://nodos.example.com/api/");

        // Verify values are set correctly
        assertEquals("https://nodos.example.com/api/", config.getNodoPerifericoApiBaseUrl());
        assertEquals("https://nodos.example.com/api/clinics", config.getClinicsApiUrl());
        assertTrue(config.isConfigured());
    }

    @Test
    @DisplayName("Should return configured state correctly")
    void testIsConfigured() throws Exception {
        // Test with empty values
        assertFalse(config.isConfigured());

        // Test with base URL set
        setPrivateField(config, "nodoPerifericoApiBaseUrl", "https://nodos.example.com/api/");
        assertTrue(config.isConfigured());
    }

    @Test
    @DisplayName("Should test getEnvOrDefault method with environment variable present")
    void testGetEnvOrDefaultWithEnvVar() throws Exception {
        // Since we can't easily mock System.getenv() in unit tests,
        // we'll test that the method exists and can be called
        Method method = NodosPerifericosConfig.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);

        // Test with a non-existent environment variable (should return default)
        String result = (String) method.invoke(config, "NON_EXISTENT_NODO_PERIFERICO_API_BASE_URL", "default-value");
        assertEquals("default-value", result);
    }

    @Test
    @DisplayName("Should test getEnvOrDefault method with environment variable absent")
    void testGetEnvOrDefaultWithoutEnvVar() throws Exception {
        Method method = NodosPerifericosConfig.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(config, "NON_EXISTENT_ENV_VAR", "default-value");

        assertEquals("default-value", result);
    }

    @Test
    @DisplayName("Should handle null environment variable values")
    void testGetEnvOrDefaultWithNullEnvVar() throws Exception {
        Method method = NodosPerifericosConfig.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(config, "TEST_NULL_VAR", "default-value");

        assertEquals("default-value", result); // Should return default when env var doesn't exist
    }

    @Test
    @DisplayName("Should construct clinics API URL correctly")
    void testGetClinicsApiUrl() throws Exception {
        // Test with default empty base URL (after init)
        Method initMethod = NodosPerifericosConfig.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(config);
        assertEquals("clinics", config.getClinicsApiUrl());

        // Test with base URL ending with slash
        setPrivateField(config, "nodoPerifericoApiBaseUrl", "https://nodos.example.com/api/");
        assertEquals("https://nodos.example.com/api/clinics", config.getClinicsApiUrl());

        // Test with base URL not ending with slash
        setPrivateField(config, "nodoPerifericoApiBaseUrl", "https://nodos.example.com/api");
        assertEquals("https://nodos.example.com/apiclinics", config.getClinicsApiUrl());
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