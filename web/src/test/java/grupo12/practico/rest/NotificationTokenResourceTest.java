package grupo12.practico.rest;

import grupo12.practico.dtos.NotificationToken.NotificationUnsubscribeRequestDTO;
import grupo12.practico.models.NotificationType;
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

    @Test
    @DisplayName("Should subscribe to notification type successfully")
    void testSubscribe() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("ACCESS_REQUEST");

        doNothing().when(notificationTokenService).subscribe(anyString(), any(NotificationType.class));

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationTokenService, times(1)).subscribe("12345678", NotificationType.ACCESS_REQUEST);
    }

    @Test
    @DisplayName("Should return 400 when subscribe request is null")
    void testSubscribeNullRequest() {
        Response response = resource.subscribe(null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }

    @Test
    @DisplayName("Should return 400 when subscribe userCi is null")
    void testSubscribeNullUserCi() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi(null);
        request.setNotificationType("ACCESS_REQUEST");

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }

    @Test
    @DisplayName("Should return 400 when subscribe userCi is empty")
    void testSubscribeEmptyUserCi() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("  ");
        request.setNotificationType("ACCESS_REQUEST");

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }

    @Test
    @DisplayName("Should return 400 when subscribe notificationType is null")
    void testSubscribeNullNotificationType() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType(null);

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("notificationType is required"));
    }

    @Test
    @DisplayName("Should return 400 when subscribe notificationType is empty")
    void testSubscribeEmptyNotificationType() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("  ");

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("notificationType is required"));
    }

    @Test
    @DisplayName("Should return 400 when subscribe notificationType is invalid")
    void testSubscribeInvalidNotificationType() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("INVALID_TYPE");

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid notificationType"));
    }

    @Test
    @DisplayName("Should return 400 when subscribe fails validation")
    void testSubscribeValidationException() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("ACCESS_REQUEST");

        doThrow(new ValidationException("Validation error")).when(notificationTokenService)
                .subscribe(anyString(), any(NotificationType.class));

        Response response = resource.subscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Validation error"));
    }

    @Test
    @DisplayName("Should unsubscribe from notification type successfully")
    void testUnsubscribe() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("CLINICAL_HISTORY_ACCESS");

        doNothing().when(notificationTokenService).unsubscribe(anyString(), any(NotificationType.class));

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(notificationTokenService, times(1)).unsubscribe("12345678", NotificationType.CLINICAL_HISTORY_ACCESS);
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe request is null")
    void testUnsubscribeNullRequest() {
        Response response = resource.unsubscribe(null);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe userCi is null")
    void testUnsubscribeNullUserCi() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi(null);
        request.setNotificationType("ACCESS_REQUEST");

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe userCi is empty")
    void testUnsubscribeEmptyUserCi() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("  ");
        request.setNotificationType("ACCESS_REQUEST");

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("userCi is required"));
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe notificationType is null")
    void testUnsubscribeNullNotificationType() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType(null);

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("notificationType is required"));
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe notificationType is empty")
    void testUnsubscribeEmptyNotificationType() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("  ");

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("notificationType is required"));
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe notificationType is invalid")
    void testUnsubscribeInvalidNotificationType() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("INVALID_TYPE");

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Invalid notificationType"));
    }

    @Test
    @DisplayName("Should return 400 when unsubscribe fails validation")
    void testUnsubscribeValidationException() {
        NotificationUnsubscribeRequestDTO request = new NotificationUnsubscribeRequestDTO();
        request.setUserCi("12345678");
        request.setNotificationType("ACCESS_REQUEST");

        doThrow(new ValidationException("Validation error")).when(notificationTokenService)
                .unsubscribe(anyString(), any(NotificationType.class));

        Response response = resource.unsubscribe(request);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Validation error"));
    }
}
