package grupo12.practico.web;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.services.Auth.OidcAuthenticationServiceLocal;
import grupo12.practico.services.Auth.OidcConfigurationService;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthBean Tests")
class AuthBeanTest {

    @Mock
    private OidcAuthenticationServiceLocal oidcService;

    @Mock
    private OidcConfigurationService oidcConfig;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    private AuthBean authBean;

    @BeforeEach
    void setUp() throws Exception {
        authBean = new AuthBean();

        // Use reflection to inject mocked dependencies
        var oidcServiceField = AuthBean.class.getDeclaredField("oidcService");
        oidcServiceField.setAccessible(true);
        oidcServiceField.set(authBean, oidcService);

        var oidcConfigField = AuthBean.class.getDeclaredField("oidcConfig");
        oidcConfigField.setAccessible(true);
        oidcConfigField.set(authBean, oidcConfig);
    }

    @Test
    @DisplayName("isConfigured - Should return true when oidcConfig is not null and configured")
    void isConfigured_ShouldReturnTrueWhenConfigured() {
        when(oidcConfig.isConfigured()).thenReturn(true);

        boolean result = authBean.isConfigured();

        assertTrue(result);
    }

    @Test
    @DisplayName("isConfigured - Should return false when oidcConfig is null")
    void isConfigured_ShouldReturnFalseWhenConfigIsNull() throws Exception {
        var oidcConfigField = AuthBean.class.getDeclaredField("oidcConfig");
        oidcConfigField.setAccessible(true);
        oidcConfigField.set(authBean, null);

        boolean result = authBean.isConfigured();

        assertFalse(result);
    }

    @Test
    @DisplayName("isConfigured - Should return false when oidcConfig is not configured")
    void isConfigured_ShouldReturnFalseWhenNotConfigured() {
        when(oidcConfig.isConfigured()).thenReturn(false);

        boolean result = authBean.isConfigured();

        assertFalse(result);
    }

    @Test
    @DisplayName("getErrorMessage - Should return error message")
    void getErrorMessage_ShouldReturnErrorMessage() {
        RuntimeException testException = new RuntimeException("Test error");
        when(oidcService.initiateAuthorization()).thenThrow(testException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            authBean.loginWithGubUy(); // This will set an error message due to mocked exception

            String result = authBean.getErrorMessage();

            assertEquals("Test error", result);
        }
    }

    @Test
    @DisplayName("loginWithGubUy - Should redirect successfully")
    void loginWithGubUy_ShouldRedirectSuccessfully() throws Exception {
        OidcAuthorizationResponseDTO response = new OidcAuthorizationResponseDTO();
        response.setAuthorizationUrl("http://example.com/auth");

        when(oidcService.initiateAuthorization()).thenReturn(response);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            authBean.loginWithGubUy();

            verify(externalContext).redirect("http://example.com/auth");
            assertNull(authBean.getErrorMessage());
        }
    }

    @Test
    @DisplayName("loginWithGubUy - Should handle exception and set error message")
    void loginWithGubUy_ShouldHandleExceptionAndSetErrorMessage() throws Exception {
        RuntimeException testException = new RuntimeException("Test error");
        when(oidcService.initiateAuthorization()).thenThrow(testException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);

            authBean.loginWithGubUy();

            assertEquals("Test error", authBean.getErrorMessage());
        }
    }
}
