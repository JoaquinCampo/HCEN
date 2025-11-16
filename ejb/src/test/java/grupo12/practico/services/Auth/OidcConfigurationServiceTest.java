package grupo12.practico.services.Auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OidcConfigurationService Tests")
class OidcConfigurationServiceTest {

    private OidcConfigurationService service;

    @BeforeEach
    void setUp() {
        service = new OidcConfigurationService();
    }

    @Test
    @DisplayName("Should initialize with default values when no environment variables are set")
    void testInitWithDefaults() throws Exception {
        // Call init method using reflection
        Method initMethod = OidcConfigurationService.class.getDeclaredMethod("init");
        initMethod.setAccessible(true);
        initMethod.invoke(service);

        // Verify default values
        assertEquals("https://auth.gub.uy/oidc/v1/authorize", service.getAuthorizeUrl());
        assertEquals("https://auth.gub.uy/oidc/v1/token", service.getTokenUrl());
        assertEquals("https://auth.gub.uy/oidc/v1/userinfo", service.getUserinfoUrl());
        assertEquals("https://auth.gub.uy/oidc/v1/jwks", service.getJwksUrl());
        assertEquals("", service.getClientId());
        assertEquals("", service.getClientSecret());
        assertEquals("", service.getRedirectUri());
        assertEquals("openid profile email", service.getScope());
        assertEquals("", service.getIssuer());
        assertEquals("https://auth.gub.uy/oidc/v1/logout", service.getLogoutUrl());
        assertEquals("", service.getPostLogoutRedirectUri());
        assertEquals("", service.getAcrValues());
        assertFalse(service.isConfigured());
    }

    @Test
    @DisplayName("Should test all getter methods return expected values")
    void testGetterMethods() throws Exception {
        // Use reflection to set private fields for testing
        setPrivateField(service, "authorizeUrl", "https://test.com/authorize");
        setPrivateField(service, "tokenUrl", "https://test.com/token");
        setPrivateField(service, "userinfoUrl", "https://test.com/userinfo");
        setPrivateField(service, "jwksUrl", "https://test.com/jwks");
        setPrivateField(service, "clientId", "test-client");
        setPrivateField(service, "clientSecret", "test-secret");
        setPrivateField(service, "redirectUri", "https://app.com/callback");
        setPrivateField(service, "scope", "openid email");
        setPrivateField(service, "issuer", "https://test.com");
        setPrivateField(service, "logoutUrl", "https://test.com/logout");
        setPrivateField(service, "postLogoutRedirectUri", "https://app.com");
        setPrivateField(service, "acrValues", "test-acr");

        // Test all getters
        assertEquals("https://test.com/authorize", service.getAuthorizeUrl());
        assertEquals("https://test.com/token", service.getTokenUrl());
        assertEquals("https://test.com/userinfo", service.getUserinfoUrl());
        assertEquals("https://test.com/jwks", service.getJwksUrl());
        assertEquals("test-client", service.getClientId());
        assertEquals("test-secret", service.getClientSecret());
        assertEquals("https://app.com/callback", service.getRedirectUri());
        assertEquals("openid email", service.getScope());
        assertEquals("https://test.com", service.getIssuer());
        assertEquals("https://test.com/logout", service.getLogoutUrl());
        assertEquals("https://app.com", service.getPostLogoutRedirectUri());
        assertEquals("test-acr", service.getAcrValues());
        assertTrue(service.isConfigured());
    }

    @Test
    @DisplayName("Should return false for isConfigured when client credentials are missing")
    void testIsConfiguredFalse() throws Exception {
        // Test with empty client ID
        setPrivateField(service, "clientId", "");
        setPrivateField(service, "clientSecret", "secret");
        assertFalse(service.isConfigured());

        // Test with empty client secret
        setPrivateField(service, "clientId", "id");
        setPrivateField(service, "clientSecret", "");
        assertFalse(service.isConfigured());

        // Test with null values
        setPrivateField(service, "clientId", null);
        setPrivateField(service, "clientSecret", "secret");
        assertFalse(service.isConfigured());

        setPrivateField(service, "clientId", "id");
        setPrivateField(service, "clientSecret", null);
        assertFalse(service.isConfigured());
    }

    @Test
    @DisplayName("Should return true for isConfigured when both client credentials are present")
    void testIsConfiguredTrue() throws Exception {
        setPrivateField(service, "clientId", "test-client");
        setPrivateField(service, "clientSecret", "test-secret");
        assertTrue(service.isConfigured());
    }

    @Test
    @DisplayName("Should test getEnvOrDefault private method exists")
    void testGetEnvOrDefaultMethodExists() throws Exception {
        Method method = OidcConfigurationService.class.getDeclaredMethod("getEnvOrDefault", String.class, String.class);
        method.setAccessible(true);
        assertNotNull(method);
    }

    @Test
    @DisplayName("Should test getFirstEnvPresent private method exists")
    void testGetFirstEnvPresentMethodExists() throws Exception {
        Method method = OidcConfigurationService.class.getDeclaredMethod("getFirstEnvPresent", String[].class,
                String.class);
        method.setAccessible(true);
        assertNotNull(method);
    }

    /**
     * Helper method to set private fields using reflection
     */
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}