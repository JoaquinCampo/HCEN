package grupo12.practico.rest;

import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("NotificationTokenResource Tests")
class NotificationTokenResourceTest {

    @Mock
    private NotificationTokenServiceLocal notificationTokenService;

    @InjectMocks
    private NotificationTokenResource resource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should register notification token")
    void testRegister() {
        when(notificationTokenService.add(any())).thenReturn(null);

        Response response = resource.register(any());

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(notificationTokenService, times(1)).add(any());
    }

    @Test
    @DisplayName("Should return 400 when registration fails validation")
    void testRegisterValidationException() {
        when(notificationTokenService.add(any())).thenThrow(new ValidationException("Invalid data"));

        Response response = resource.register(any());

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid data"));
    }

    @Test
    @DisplayName("Should unregister notification token")
    void testUnregister() {
        doNothing().when(notificationTokenService).delete(any());

        Response response = resource.unregister("12345678", "token123");

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationTokenService, times(1)).delete(any());
    }

    @Test
    @DisplayName("Should get subscription preferences")
    void testGetSubscriptionPreferences() {
        when(notificationTokenService.getSubscriptionPreferences(anyString())).thenReturn(null);

        Response response = resource.getSubscriptionPreferences("12345678");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(notificationTokenService, times(1)).getSubscriptionPreferences("12345678");
    }

    @Test
    @DisplayName("Should return 400 when getting preferences with empty userCi")
    void testGetSubscriptionPreferencesEmptyUserCi() {
        Response response = resource.getSubscriptionPreferences("  ");

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }
}
