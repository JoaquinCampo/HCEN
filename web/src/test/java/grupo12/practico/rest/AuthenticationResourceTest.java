package grupo12.practico.rest;

import grupo12.practico.dtos.Auth.OidcAuthorizationResponseDTO;
import grupo12.practico.dtos.Auth.OidcAuthResultDTO;
import grupo12.practico.dtos.Auth.OidcUserInfoDTO;
import grupo12.practico.services.Auth.OidcAuthenticationServiceLocal;
import grupo12.practico.services.HcenAdmin.HcenAdminServiceLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("AuthenticationResource Tests")
class AuthenticationResourceTest {

    @Mock
    private OidcAuthenticationServiceLocal oidcAuthenticationService;

    @Mock
    private HcenAdminServiceLocal hcenAdminService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthenticationResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("initiateGubuyAuth - Should return authorization response successfully")
    void testInitiateGubuyAuth_Success() {
        // Arrange
        OidcAuthorizationResponseDTO authResponse = new OidcAuthorizationResponseDTO();
        authResponse.setAuthorizationUrl("https://gub.uy/auth");
        authResponse.setState("test-state");

        when(oidcAuthenticationService.initiateAuthorization()).thenReturn(authResponse);

        // Act
        Response response = resource.initiateGubuyAuth();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(authResponse, response.getEntity());
        verify(oidcAuthenticationService).initiateAuthorization();
    }

    @Test
    @DisplayName("initiateGubuyAuth - Should return service unavailable when OIDC not configured")
    void testInitiateGubuyAuth_OidcNotConfigured() {
        // Arrange
        when(oidcAuthenticationService.initiateAuthorization())
                .thenThrow(new IllegalStateException("OIDC not configured"));

        // Act
        Response response = resource.initiateGubuyAuth();

        // Assert
        assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("OIDC authentication is not configured"));
        verify(oidcAuthenticationService).initiateAuthorization();
    }

    @Test
    @DisplayName("initiateGubuyAuth - Should return internal server error on general exception")
    void testInitiateGubuyAuth_GeneralException() {
        // Arrange
        when(oidcAuthenticationService.initiateAuthorization())
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act
        Response response = resource.initiateGubuyAuth();

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Failed to initiate authorization"));
        verify(oidcAuthenticationService).initiateAuthorization();
    }

    @Test
    @DisplayName("handleGubuyCallback - Should authenticate successfully and return auth result")
    void testHandleGubuyCallback_Success() throws Exception {
        // Arrange
        String code = "auth-code";
        String state = "valid-state";
        OidcAuthResultDTO authResult = new OidcAuthResultDTO();
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.setEmail("test@gub.uy");
        authResult.setUserInfo(userInfo);
        authResult.setIdToken("id-token");
        authResult.setAccessToken("access-token");
        authResult.setLogoutUrl("https://gub.uy/logout");

        doReturn(authResult).when(oidcAuthenticationService).handleCallback(code, state);
        when(request.getSession(true)).thenReturn(session);
        when(request.getHeader("Accept")).thenReturn("application/json");

        // Act
        Response response = resource.handleGubuyCallback(code, state, null, null);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(authResult, response.getEntity());
        verify(oidcAuthenticationService).handleCallback(code, state);
        verify(session).setAttribute("authenticated", Boolean.TRUE);
        verify(session).setAttribute("id_token", "id-token");
        verify(session).setAttribute("access_token", "access-token");
        verify(session).setAttribute("user_info", userInfo);
        verify(session).setAttribute(eq("id_token_claims"), any());
        verify(session).setAttribute("logout_url", "https://gub.uy/logout");
    }

    @Test
    @DisplayName("handleGubuyCallback - Should redirect to home when Accept header is not JSON")
    void testHandleGubuyCallback_RedirectWhenNotJson() throws Exception {
        // Arrange
        String code = "auth-code";
        String state = "valid-state";
        OidcAuthResultDTO authResult = new OidcAuthResultDTO();

        doReturn(authResult).when(oidcAuthenticationService).handleCallback(code, state);
        when(request.getSession(true)).thenReturn(session);
        when(request.getHeader("Accept")).thenReturn("text/html");
        when(request.getContextPath()).thenReturn("");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);

        // Act
        Response response = resource.handleGubuyCallback(code, state, null, null);

        // Assert
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
        verify(oidcAuthenticationService).handleCallback(code, state);
    }

    @Test
    @DisplayName("handleGubuyCallback - Should return bad request when authorization error from provider")
    void testHandleGubuyCallback_AuthorizationError() {
        // Arrange
        String error = "access_denied";
        String errorDescription = "User denied access";

        // Act
        Response response = resource.handleGubuyCallback("code", "state", error, errorDescription);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Authorization failed"));
    }

    @Test
    @DisplayName("handleGubuyCallback - Should return bad request when code is missing")
    void testHandleGubuyCallback_MissingCode() {
        // Act
        Response response = resource.handleGubuyCallback(null, "state", null, null);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Missing authorization code"));
    }

    @Test
    @DisplayName("handleGubuyCallback - Should return bad request when state is missing")
    void testHandleGubuyCallback_MissingState() {
        // Act
        Response response = resource.handleGubuyCallback("code", null, null, null);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Missing state parameter"));
    }

    @Test
    @DisplayName("handleGubuyCallback - Should return bad request on invalid state")
    void testHandleGubuyCallback_InvalidState() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid state")).when(oidcAuthenticationService)
                .handleCallback(anyString(), anyString());

        // Act
        Response response = resource.handleGubuyCallback("code", "invalid-state", null, null);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid state parameter"));
    }

    @Test
    @DisplayName("handleGubuyCallback - Should return internal server error on authentication failure")
    void testHandleGubuyCallback_AuthenticationFailure() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Authentication failed")).when(oidcAuthenticationService)
                .handleCallback(anyString(), anyString());

        // Act
        Response response = resource.handleGubuyCallback("code", "state", null, null);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Authentication failed"));
    }

    @Test
    @DisplayName("me - Should return user info when authenticated")
    void testMe_Authenticated() {
        // Arrange
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.setEmail("test@gub.uy");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("authenticated")).thenReturn(Boolean.TRUE);
        when(session.getAttribute("user_info")).thenReturn(userInfo);

        // Act
        Response response = resource.me();

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(userInfo, response.getEntity());
    }

    @Test
    @DisplayName("me - Should return unauthorized when not authenticated")
    void testMe_NotAuthenticated() {
        // Arrange
        when(request.getSession(false)).thenReturn(null);

        // Act
        Response response = resource.me();

        // Assert
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Not authenticated"));
    }

    @Test
    @DisplayName("logout - Should redirect to provider logout URL")
    void testLogout_WithLogoutUrl() {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("logout_url")).thenReturn("https://gub.uy/logout");

        // Act
        Response response = resource.logout();

        // Assert
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
        verify(session).invalidate();
    }

    @Test
    @DisplayName("logout - Should build logout URL from ID token when logout URL not in session")
    void testLogout_BuildLogoutUrl() {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("id_token")).thenReturn("id-token");
        when(session.getAttribute("logout_url")).thenReturn(null);
        when(oidcAuthenticationService.buildLogoutUrl("id-token")).thenReturn("https://gub.uy/logout");

        // Act
        Response response = resource.logout();

        // Assert
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
        verify(oidcAuthenticationService).buildLogoutUrl("id-token");
        verify(session).invalidate();
    }

    @Test
    @DisplayName("logout - Should redirect to home when no logout URL available")
    void testLogout_RedirectToHome() {
        // Arrange
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("id_token")).thenReturn(null);
        when(session.getAttribute("logout_url")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);

        // Act
        Response response = resource.logout();

        // Assert
        assertEquals(Response.Status.SEE_OTHER.getStatusCode(), response.getStatus());
        verify(session).invalidate();
    }
}