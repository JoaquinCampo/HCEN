package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.services.Auth.OidcAuthenticationServiceLocal;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationBean Tests")
class AuthenticationBeanTest {

    @Mock
    private OidcAuthenticationServiceLocal oidcAuthenticationService;

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    @Mock
    private Flash flash;

    private AuthenticationBean authBean;

    @BeforeEach
    void setUp() throws Exception {
        authBean = new AuthenticationBean();

        // Use reflection to inject mocked dependencies
        var serviceField = AuthenticationBean.class.getDeclaredField("oidcAuthenticationService");
        serviceField.setAccessible(true);
        serviceField.set(authBean, oidcAuthenticationService);
    }

    @Test
    @DisplayName("init - Should initialize and check configuration")
    void init_ShouldInitializeAndCheckConfiguration() {
        // This test verifies that init() method can be called without exceptions
        assertDoesNotThrow(() -> authBean.init());
    }

    @Test
    @DisplayName("isConfigured - Should return configured status")
    void isConfigured_ShouldReturnConfiguredStatus() {
        // Access private field to set configured status
        assertDoesNotThrow(() -> {
            var configuredField = AuthenticationBean.class.getDeclaredField("configured");
            configuredField.setAccessible(true);
            configuredField.set(authBean, true);
        });

        assertTrue(authBean.isConfigured());
    }

    @Test
    @DisplayName("getErrorMessage - Should return error message")
    void getErrorMessage_ShouldReturnErrorMessage() {
        assertDoesNotThrow(() -> {
            var errorMessageField = AuthenticationBean.class.getDeclaredField("errorMessage");
            errorMessageField.setAccessible(true);
            errorMessageField.set(authBean, "Test error");
        });

        assertEquals("Test error", authBean.getErrorMessage());
    }

    @Test
    @DisplayName("isShowHcenAdminError - Should return showHcenAdminError status")
    void isShowHcenAdminError_ShouldReturnShowHcenAdminErrorStatus() {
        assertDoesNotThrow(() -> {
            var showHcenAdminErrorField = AuthenticationBean.class.getDeclaredField("showHcenAdminError");
            showHcenAdminErrorField.setAccessible(true);
            showHcenAdminErrorField.set(authBean, true);
        });

        assertTrue(authBean.isShowHcenAdminError());
    }

    @Test
    @DisplayName("loginWithGubUy - Should redirect successfully")
    void loginWithGubUy_ShouldRedirectSuccessfully() throws Exception {
        OidcAuthorizationResponseDTO response = new OidcAuthorizationResponseDTO();
        response.setAuthorizationUrl("http://example.com/auth");
        response.setState("test-state");

        when(oidcAuthenticationService.initiateAuthorization()).thenReturn(response);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);

            authBean.loginWithGubUy();

            verify(sessionMap).put("oidc_state", "test-state");
            verify(externalContext).redirect("http://example.com/auth");
            verify(facesContext).responseComplete();
        }
    }

    @Test
    @DisplayName("loginWithGubUy - Should handle IllegalStateException and add error message")
    void loginWithGubUy_ShouldHandleIllegalStateExceptionAndAddErrorMessage() throws Exception {
        IllegalStateException testException = new IllegalStateException("OIDC not configured");
        when(oidcAuthenticationService.initiateAuthorization()).thenThrow(testException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            authBean.loginWithGubUy();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
        }
    }

    @Test
    @DisplayName("loginWithGubUy - Should handle IOException and add error message")
    void loginWithGubUy_ShouldHandleIOExceptionAndAddErrorMessage() throws Exception {
        IOException testException = new IOException("Redirect failed");
        when(oidcAuthenticationService.initiateAuthorization()).thenReturn(new OidcAuthorizationResponseDTO());

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            doThrow(testException).when(externalContext).redirect(anyString());

            authBean.loginWithGubUy();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
        }
    }

    @Test
    @DisplayName("loginWithGubUy - Should handle generic exception and add error message")
    void loginWithGubUy_ShouldHandleGenericExceptionAndAddErrorMessage() throws Exception {
        RuntimeException testException = new RuntimeException("Unexpected error");
        when(oidcAuthenticationService.initiateAuthorization()).thenThrow(testException);

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            authBean.loginWithGubUy();

            verify(facesContext).addMessage(eq(null), any(FacesMessage.class));
        }
    }

    @Test
    @DisplayName("logout - Should redirect to logout URL from session")
    void logout_ShouldRedirectToLogoutURLFromSession() throws Exception {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("logout_url")).thenReturn("http://example.com/logout");

            authBean.logout();

            verify(externalContext).invalidateSession();
            verify(externalContext).redirect("http://example.com/logout");
            verify(facesContext).responseComplete();
        }
    }

    @Test
    @DisplayName("logout - Should invalidate session when no logout URL")
    void logout_ShouldInvalidateSessionWhenNoLogoutURL() throws Exception {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("logout_url")).thenReturn(null);

            authBean.logout();

            verify(externalContext).invalidateSession();
            verify(externalContext, never()).redirect(anyString());
            verify(facesContext).responseComplete();
        }
    }

    @Test
    @DisplayName("logout - Should handle IOException")
    void logout_ShouldHandleIOException() throws Exception {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("logout_url")).thenReturn("http://example.com/logout");
            doThrow(new IOException("Redirect failed")).when(externalContext).redirect(anyString());

            // Should not throw exception
            assertDoesNotThrow(() -> authBean.logout());
        }
    }
}
