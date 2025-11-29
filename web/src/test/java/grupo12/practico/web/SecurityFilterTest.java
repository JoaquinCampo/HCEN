package grupo12.practico.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SecurityFilter Tests")
class SecurityFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        securityFilter = new SecurityFilter();
    }

    // OIDC callback redirect tests

    @Test
    @DisplayName("doFilter - Should redirect root path with code parameter to auth callback")
    void doFilter_ShouldRedirectRootPathWithCodeToAuthCallback() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getParameter("code")).thenReturn("auth_code_123");
        when(request.getParameter("error")).thenReturn(null);
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn("code=auth_code_123&state=some_state");

        securityFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/api/auth/gubuy/callback?code=auth_code_123&state=some_state");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilter - Should redirect empty path with code parameter to auth callback")
    void doFilter_ShouldRedirectEmptyPathWithCodeToAuthCallback() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("");
        when(request.getParameter("code")).thenReturn("auth_code_123");
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn("code=auth_code_123");

        securityFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/api/auth/gubuy/callback?code=auth_code_123");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilter - Should redirect root path with error parameter to auth callback")
    void doFilter_ShouldRedirectRootPathWithErrorToAuthCallback() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn("access_denied");
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn("error=access_denied&error_description=User%20denied");

        securityFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/api/auth/gubuy/callback?error=access_denied&error_description=User%20denied");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilter - Should redirect with context path")
    void doFilter_ShouldRedirectWithContextPath() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getParameter("code")).thenReturn("auth_code_123");
        when(request.getContextPath()).thenReturn("/myapp");
        when(request.getQueryString()).thenReturn("code=auth_code_123");

        securityFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/myapp/api/auth/gubuy/callback?code=auth_code_123");
    }

    @Test
    @DisplayName("doFilter - Should handle null query string in redirect")
    void doFilter_ShouldHandleNullQueryStringInRedirect() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getParameter("code")).thenReturn("auth_code_123");
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/api/auth/gubuy/callback");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilter - Should handle empty query string in redirect")
    void doFilter_ShouldHandleEmptyQueryStringInRedirect() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getParameter("code")).thenReturn("auth_code_123");
        when(request.getContextPath()).thenReturn("");
        when(request.getQueryString()).thenReturn("");

        securityFilter.doFilter(request, response, filterChain);

        verify(response).sendRedirect("/api/auth/gubuy/callback");
        verify(filterChain, never()).doFilter(request, response);
    }

    // Public path tests

    @Test
    @DisplayName("doFilter - Should allow API endpoints")
    void doFilter_ShouldAllowApiEndpoints() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/api/health-users");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow auth gubuy endpoints")
    void doFilter_ShouldAllowAuthGubuyEndpoints() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/auth/gubuy/callback");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow JSF resources (javax)")
    void doFilter_ShouldAllowJsfResourcesJavax() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/javax.faces.resource/some.css");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow JSF resources (jakarta)")
    void doFilter_ShouldAllowJsfResourcesJakarta() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/jakarta.faces.resource/script.js");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow login page")
    void doFilter_ShouldAllowLoginPage() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/auth/login.xhtml");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow callback page")
    void doFilter_ShouldAllowCallbackPage() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/auth/callback.xhtml");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow callback path")
    void doFilter_ShouldAllowCallbackPath() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/callback");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow logout path")
    void doFilter_ShouldAllowLogoutPath() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/logout");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow root path without auth params")
    void doFilter_ShouldAllowRootPathWithoutAuthParams() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow index.xhtml")
    void doFilter_ShouldAllowIndexXhtml() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/index.xhtml");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow index.jsf")
    void doFilter_ShouldAllowIndexJsf() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/index.jsf");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    // Static resource tests

    @Test
    @DisplayName("doFilter - Should allow CSS files")
    void doFilter_ShouldAllowCssFiles() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/resources/css/style.css");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow JS files")
    void doFilter_ShouldAllowJsFiles() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/resources/js/app.js");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow PNG images")
    void doFilter_ShouldAllowPngImages() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/images/logo.png");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow JPG images")
    void doFilter_ShouldAllowJpgImages() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/images/photo.jpg");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow JPEG images")
    void doFilter_ShouldAllowJpegImages() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/images/photo.jpeg");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow SVG files")
    void doFilter_ShouldAllowSvgFiles() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/images/icon.svg");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow ICO files")
    void doFilter_ShouldAllowIcoFiles() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/favicon.ico");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    // Non-public path tests (should still be allowed as login is optional)

    @Test
    @DisplayName("doFilter - Should allow protected paths (login optional)")
    void doFilter_ShouldAllowProtectedPaths() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/admin/dashboard.xhtml");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("doFilter - Should allow any path when no auth params present")
    void doFilter_ShouldAllowAnyPathWhenNoAuthParamsPresent() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/some/protected/resource");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    // Case handling tests

    @Test
    @DisplayName("doFilter - Should handle uppercase file extensions (case insensitive check)")
    void doFilter_ShouldHandleUppercaseFileExtensions() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/images/logo.PNG");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        // The filter uses toLowerCase so .PNG should be allowed
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("doFilter - Should handle mixed case API path")
    void doFilter_ShouldHandleMixedCaseApiPath() throws IOException, ServletException {
        when(request.getRequestURI()).thenReturn("/API/health-users");
        when(request.getParameter("code")).thenReturn(null);
        when(request.getParameter("error")).thenReturn(null);

        securityFilter.doFilter(request, response, filterChain);

        // The filter uses toLowerCase so /API/ should be allowed
        verify(filterChain).doFilter(request, response);
    }
}
