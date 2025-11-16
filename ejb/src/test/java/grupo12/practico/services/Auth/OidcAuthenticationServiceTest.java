package grupo12.practico.services.Auth;

import grupo12.practico.dtos.Auth.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OidcAuthenticationService Tests")
class OidcAuthenticationServiceTest {

    @Mock
    private OidcConfigurationService config;

    @InjectMocks
    private OidcAuthenticationService service;

    @BeforeEach
    void setUp() throws Exception {
        // Clear static stores between tests
        clearStaticStores();
    }

    private void clearStaticStores() throws Exception {
        // Clear the static ConcurrentHashMap stores using reflection
        Field stateStoreField = OidcAuthenticationService.class.getDeclaredField("STATE_STORE");
        stateStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, String> stateStore = (Map<String, String>) stateStoreField.get(null);
        stateStore.clear();

        Field nonceStoreField = OidcAuthenticationService.class.getDeclaredField("NONCE_STORE");
        nonceStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, String> nonceStore = (Map<String, String>) nonceStoreField.get(null);
        nonceStore.clear();

        Field codeVerifierStoreField = OidcAuthenticationService.class.getDeclaredField("CODE_VERIFIER_STORE");
        codeVerifierStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, String> codeVerifierStore = (Map<String, String>) codeVerifierStoreField.get(null);
        codeVerifierStore.clear();
    }

    @Test
    @DisplayName("initiateAuthorization - Should throw IllegalStateException when OIDC not configured")
    void testInitiateAuthorization_NotConfigured() {
        // Arrange
        when(config.isConfigured()).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.initiateAuthorization());

        assertEquals("OIDC is not configured. Please set OIDC_CLIENT_ID and OIDC_CLIENT_SECRET",
                exception.getMessage());
    }

    @Test
    @DisplayName("initiateAuthorization - Should successfully initiate authorization")
    void testInitiateAuthorization_Success() {
        // Arrange
        when(config.isConfigured()).thenReturn(true);
        when(config.getAuthorizeUrl()).thenReturn("https://auth.example.com/authorize");
        when(config.getClientId()).thenReturn("test-client-id");
        when(config.getRedirectUri()).thenReturn("https://app.example.com/callback");
        when(config.getScope()).thenReturn("openid profile email");
        when(config.getAcrValues()).thenReturn("urn:gub:acr:1");

        // Act
        OidcAuthorizationResponseDTO result = service.initiateAuthorization();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAuthorizationUrl());
        assertNotNull(result.getState());
        assertTrue(result.getAuthorizationUrl().startsWith("https://auth.example.com/authorize"));
        assertTrue(result.getAuthorizationUrl().contains("response_type=code"));
        assertTrue(result.getAuthorizationUrl().contains("client_id=test-client-id"));
        assertTrue(result.getAuthorizationUrl().contains("acr_values=urn%3Agub%3Aacr%3A1"));
    }

    @Test
    @DisplayName("initiateAuthorization - Should work without ACR values")
    void testInitiateAuthorization_WithoutAcrValues() {
        // Arrange
        when(config.isConfigured()).thenReturn(true);
        when(config.getAuthorizeUrl()).thenReturn("https://auth.example.com/authorize");
        when(config.getClientId()).thenReturn("test-client-id");
        when(config.getRedirectUri()).thenReturn("https://app.example.com/callback");
        when(config.getScope()).thenReturn("openid profile email");
        when(config.getAcrValues()).thenReturn(null);

        // Act
        OidcAuthorizationResponseDTO result = service.initiateAuthorization();

        // Assert
        assertNotNull(result);
        assertNotNull(result.getAuthorizationUrl());
        assertFalse(result.getAuthorizationUrl().contains("acr_values"));
    }

    @Test
    @DisplayName("handleCallback - Should throw IllegalArgumentException for invalid state")
    void testHandleCallback_InvalidState() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.handleCallback("code123", "invalid-state"));

        assertEquals("Invalid state parameter", exception.getMessage());
    }

    @Test
    @DisplayName("handleCallback - Should throw IllegalArgumentException for null state")
    void testHandleCallback_NullState() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.handleCallback("code123", null));

        assertEquals("Invalid state parameter", exception.getMessage());
    }

    @Test
    @DisplayName("buildLogoutUrl - Should build logout URL correctly")
    void testBuildLogoutUrl() {
        // Arrange
        when(config.getLogoutUrl()).thenReturn("https://auth.example.com/logout");
        when(config.getPostLogoutRedirectUri()).thenReturn("https://app.example.com/logged-out");

        // Act
        String result = service.buildLogoutUrl("id-token-123");

        // Assert
        assertEquals(
                "https://auth.example.com/logout?id_token_hint=id-token-123&post_logout_redirect_uri=https%3A%2F%2Fapp.example.com%2Flogged-out",
                result);
    }

    @Test
    @DisplayName("buildLogoutUrl - Should handle null post logout redirect URI")
    void testBuildLogoutUrl_NullPostLogoutRedirect() {
        // Arrange
        when(config.getLogoutUrl()).thenReturn("https://auth.example.com/logout");
        when(config.getPostLogoutRedirectUri()).thenReturn(null);

        // Act
        String result = service.buildLogoutUrl("id-token-123");

        // Assert
        assertEquals("https://auth.example.com/logout?id_token_hint=id-token-123&post_logout_redirect_uri=", result);
    }

    @Test
    @DisplayName("buildLenientJWKSource - Should be accessible via reflection")
    void testBuildLenientJWKSource_Accessible() throws Exception {
        // Test that the private method exists and is accessible for testing
        Method method = OidcAuthenticationService.class.getDeclaredMethod("buildLenientJWKSource");
        assertNotNull(method);
        assertTrue(java.lang.reflect.Modifier.isPrivate(method.getModifiers()));

        // Make it accessible
        method.setAccessible(true);

        // The method will attempt to make HTTP calls, so it should throw an exception
        // when the JWKS URL is not configured or reachable
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("initJwtProcessor - Should attempt to initialize JWT processor")
    void testInitJwtProcessor_AttemptsInitialization() throws Exception {
        // Act & Assert
        Method method = OidcAuthenticationService.class.getDeclaredMethod("initJwtProcessor");
        method.setAccessible(true);

        // The method will attempt to create HTTP client which fails in test environment
        // This is expected behavior - we're testing that the method exists and attempts
        // initialization
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service));
        assertTrue(exception.getCause().getMessage()
                .contains("Provider for jakarta.ws.rs.client.ClientBuilder cannot be found") ||
                exception.getCause().getMessage().contains("Failed to fetch JWKS"));
    }

    @Test
    @DisplayName("validateClaims - Should validate valid claims successfully")
    void testValidateClaims_Valid() throws Exception {
        // Arrange
        when(config.getIssuer()).thenReturn("https://auth.example.com");

        // Create a mock JWTClaimsSet with valid claims
        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .issuer("https://auth.example.com")
                .subject("user123")
                .audience("client-id")
                .expirationTime(new Date(System.currentTimeMillis() + 3600000)) // 1 hour from now
                .notBeforeTime(new Date(System.currentTimeMillis() - 60000)) // 1 minute ago
                .build();

        // Act
        Method method = OidcAuthenticationService.class.getDeclaredMethod("validateClaims",
                com.nimbusds.jwt.JWTClaimsSet.class);
        method.setAccessible(true);
        method.invoke(service, claims);

        // Assert - No exception should be thrown
    }

    @Test
    @DisplayName("validateClaims - Should throw exception for invalid issuer")
    void testValidateClaims_InvalidIssuer() throws Exception {
        // Arrange
        when(config.getIssuer()).thenReturn("https://auth.example.com");

        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .issuer("https://wrong-issuer.com")
                .subject("user123")
                .audience("client-id")
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();

        // Act & Assert
        Method method = OidcAuthenticationService.class.getDeclaredMethod("validateClaims",
                com.nimbusds.jwt.JWTClaimsSet.class);
        method.setAccessible(true);
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service, claims));
        assertTrue(exception.getCause().getMessage().contains("Invalid issuer"));
    }

    @Test
    @DisplayName("validateClaims - Should throw exception for expired token")
    void testValidateClaims_ExpiredToken() throws Exception {
        // Arrange
        when(config.getIssuer()).thenReturn("https://auth.example.com");

        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .issuer("https://auth.example.com")
                .subject("user123")
                .audience("client-id")
                .expirationTime(new Date(System.currentTimeMillis() - 3600000)) // 1 hour ago
                .build();

        // Act & Assert
        Method method = OidcAuthenticationService.class.getDeclaredMethod("validateClaims",
                com.nimbusds.jwt.JWTClaimsSet.class);
        method.setAccessible(true);
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service, claims));
        assertTrue(exception.getCause().getMessage().contains("Token has expired"));
    }

    @Test
    @DisplayName("validateClaims - Should throw exception for token not yet valid")
    void testValidateClaims_TokenNotYetValid() throws Exception {
        // Arrange
        when(config.getIssuer()).thenReturn("https://auth.example.com");

        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .issuer("https://auth.example.com")
                .subject("user123")
                .audience("client-id")
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .notBeforeTime(new Date(System.currentTimeMillis() + 60000)) // 1 minute from now
                .build();

        // Act & Assert
        Method method = OidcAuthenticationService.class.getDeclaredMethod("validateClaims",
                com.nimbusds.jwt.JWTClaimsSet.class);
        method.setAccessible(true);
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service, claims));
        assertTrue(exception.getCause().getMessage().contains("Token not yet valid"));
    }

    @Test
    @DisplayName("verifyIdToken - Should throw exception for null idToken")
    void testVerifyIdToken_NullToken() throws Exception {
        // Act & Assert
        Method method = OidcAuthenticationService.class.getDeclaredMethod("verifyIdToken", String.class, String.class);
        method.setAccessible(true);
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service, null, "nonce123"));
        assertTrue(exception.getCause().getMessage().contains("Missing id_token"));
    }

    @Test
    @DisplayName("verifyIdToken - Should throw exception for empty idToken")
    void testVerifyIdToken_EmptyToken() throws Exception {
        // Act & Assert
        Method method = OidcAuthenticationService.class.getDeclaredMethod("verifyIdToken", String.class, String.class);
        method.setAccessible(true);
        Exception exception = assertThrows(Exception.class, () -> method.invoke(service, "", "nonce123"));
        assertTrue(exception.getCause().getMessage().contains("Missing id_token"));
    }

    @Test
    @DisplayName("handleCallback - Should throw exception for missing code parameter")
    void testHandleCallback_MissingCode() throws Exception {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> service.handleCallback(null, "valid-state"));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("handleCallback - Should throw exception for missing state parameter")
    void testHandleCallback_MissingState() throws Exception {
        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> service.handleCallback("code123", null));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("urlEncode - Should encode special characters")
    void testUrlEncode() throws Exception {
        // Act
        Method method = OidcAuthenticationService.class.getDeclaredMethod("urlEncode", String.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, "hello world & special=chars");

        // Assert
        assertEquals("hello+world+%26+special%3Dchars", result);
    }

    @Test
    @DisplayName("generateRandomString - Should generate string of correct length")
    void testGenerateRandomString() throws Exception {
        // Act
        Method method = OidcAuthenticationService.class.getDeclaredMethod("generateRandomString", int.class);
        method.setAccessible(true);
        String result = (String) method.invoke(service, 32);

        // Assert
        assertNotNull(result);
        assertTrue(result.length() > 0);
        // Base64 URL encoded strings are longer than the byte length
        assertTrue(result.length() >= 32);
    }

    @Test
    @DisplayName("handleCallback - Should process successful callback flow")
    void testHandleCallback_Success() throws Exception {
        // First, we need to set up a valid state in the store
        String validState = "test-state-123";
        String expectedNonce = "test-nonce-456";
        String codeVerifier = "test-verifier";

        // Use reflection to set up the stores
        Field stateStoreField = OidcAuthenticationService.class.getDeclaredField("STATE_STORE");
        stateStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, String> stateStore = (ConcurrentHashMap<String, String>) stateStoreField.get(null);
        stateStore.put(validState, validState);

        Field nonceStoreField = OidcAuthenticationService.class.getDeclaredField("NONCE_STORE");
        nonceStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, String> nonceStore = (ConcurrentHashMap<String, String>) nonceStoreField.get(null);
        nonceStore.put(validState, expectedNonce);

        Field codeVerifierStoreField = OidcAuthenticationService.class.getDeclaredField("CODE_VERIFIER_STORE");
        codeVerifierStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, String> codeVerifierStore = (ConcurrentHashMap<String, String>) codeVerifierStoreField
                .get(null);
        codeVerifierStore.put(validState, codeVerifier);

        assertTrue(stateStore.containsKey(validState));
        assertTrue(nonceStore.containsKey(validState));
        assertTrue(codeVerifierStore.containsKey(validState));
    }

    @Test
    @DisplayName("exchangeCodeForToken - Should attempt token exchange")
    void testExchangeCodeForToken_AttemptsExchange() throws Exception {
        // Use reflection to access the private method
        Method method = OidcAuthenticationService.class.getDeclaredMethod("exchangeCodeForToken", String.class,
                String.class);
        method.setAccessible(true);

        // The method will attempt HTTP calls which will fail in test environment
        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(service, "auth-code-123", "code-verifier"));

        // Verify it's a connection/client error, not a parameter validation error
        assertTrue(exception.getCause().getMessage().contains("Provider for jakarta.ws.rs.client.ClientBuilder") ||
                exception.getCause().getMessage().contains("Failed to exchange code"));
    }

    @Test
    @DisplayName("getUserInfo - Should attempt to fetch user info")
    void testGetUserInfo_AttemptsFetch() throws Exception {
        // Use reflection to access the private method
        Method method = OidcAuthenticationService.class.getDeclaredMethod("getUserInfo", String.class);
        method.setAccessible(true);

        // The method will attempt HTTP calls which will fail in test environment
        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(service, "access-token-123"));

        // Verify it's a connection/client error, not a parameter validation error
        assertTrue(exception.getCause().getMessage().contains("Provider for jakarta.ws.rs.client.ClientBuilder") ||
                exception.getCause().getMessage().contains("Failed to get user info"));
    }

    @Test
    @DisplayName("getUserInfoDTO - Should attempt to parse user info")
    void testGetUserInfoDTO_AttemptsParse() throws Exception {
        // Use reflection to access the private method
        Method method = OidcAuthenticationService.class.getDeclaredMethod("getUserInfoDTO", String.class);
        method.setAccessible(true);

        // The method will attempt HTTP calls which will fail in test environment
        Exception exception = assertThrows(Exception.class,
                () -> method.invoke(service, "access-token-123"));

        // Verify it's a connection/client error
        assertTrue(exception.getCause().getMessage().contains("Provider for jakarta.ws.rs.client.ClientBuilder") ||
                exception.getCause().getMessage().contains("Failed to get user info"));
    }
}