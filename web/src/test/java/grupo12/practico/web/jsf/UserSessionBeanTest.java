package grupo12.practico.web.jsf;

import grupo12.practico.dtos.Auth.OidcUserInfoDTO;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserSessionBean Tests")
class UserSessionBeanTest {

    @Mock
    private FacesContext facesContext;

    @Mock
    private ExternalContext externalContext;

    private UserSessionBean userSessionBean;

    @BeforeEach
    void setUp() {
        userSessionBean = new UserSessionBean();
    }

    @Test
    @DisplayName("isAuthenticated - Should return true when authenticated is true")
    void isAuthenticated_ShouldReturnTrueWhenAuthenticatedIsTrue() {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("authenticated")).thenReturn(Boolean.TRUE);

            assertTrue(userSessionBean.isAuthenticated());
        }
    }

    @Test
    @DisplayName("isAuthenticated - Should return false when authenticated is false")
    void isAuthenticated_ShouldReturnFalseWhenAuthenticatedIsFalse() {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("authenticated")).thenReturn(Boolean.FALSE);

            assertFalse(userSessionBean.isAuthenticated());
        }
    }

    @Test
    @DisplayName("isAuthenticated - Should return false when authenticated is null")
    void isAuthenticated_ShouldReturnFalseWhenAuthenticatedIsNull() {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("authenticated")).thenReturn(null);

            assertFalse(userSessionBean.isAuthenticated());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return full name when available")
    void getDisplayName_ShouldReturnFullNameWhenAvailable() {
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.seFullName("John Doe");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("John Doe", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return composed name from first and last name")
    void getDisplayName_ShouldReturnComposedNameFromFirstAndLastName() {
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.setfirstName("John");
        userInfo.setFirstLastName("Doe");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("John Doe", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return nickname when no name available")
    void getDisplayName_ShouldReturnNicknameWhenNoNameAvailable() {
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.setNickname("johndoe");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("johndoe", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return email when no name or nickname available")
    void getDisplayName_ShouldReturnEmailWhenNoNameOrNicknameAvailable() {
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.setEmail("john@example.com");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("john@example.com", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return id when no other info available")
    void getDisplayName_ShouldReturnIdWhenNoOtherInfoAvailable() {
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();
        userInfo.setId("12345678");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("12345678", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return default when no info available")
    void getDisplayName_ShouldReturnDefaultWhenNoInfoAvailable() {
        OidcUserInfoDTO userInfo = new OidcUserInfoDTO();

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("Usuario", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should handle Map fallback with full name")
    void getDisplayName_ShouldHandleMapFallbackWithFullName() {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nombre_completo", "John Doe");

        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(userInfo);

            assertEquals("John Doe", userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getDisplayName - Should return null when user_info is null")
    void getDisplayName_ShouldReturnNullWhenUserInfoIsNull() {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("user_info")).thenReturn(null);

            assertNull(userSessionBean.getDisplayName());
        }
    }

    @Test
    @DisplayName("getLogoutHref - Should return logout URL from session")
    void getLogoutHref_ShouldReturnLogoutURLFromSession() {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("logout_url")).thenReturn("http://example.com/logout");

            assertEquals("http://example.com/logout", userSessionBean.getLogoutHref());
        }
    }

    @Test
    @DisplayName("getLogoutHref - Should return fallback URL when no logout URL in session")
    void getLogoutHref_ShouldReturnFallbackURLWhenNoLogoutURLInSession() {
        try (MockedStatic<FacesContext> facesContextMockedStatic = mockStatic(FacesContext.class)) {
            facesContextMockedStatic.when(FacesContext::getCurrentInstance).thenReturn(facesContext);
            when(facesContext.getExternalContext()).thenReturn(externalContext);
            when(externalContext.getRequestContextPath()).thenReturn("/myapp");

            @SuppressWarnings("unchecked")
            Map<String, Object> sessionMap = mock(Map.class);
            when(externalContext.getSessionMap()).thenReturn(sessionMap);
            when(sessionMap.get("logout_url")).thenReturn(null);

            assertEquals("/myapp/api/auth/gubuy/logout", userSessionBean.getLogoutHref());
        }
    }
}
