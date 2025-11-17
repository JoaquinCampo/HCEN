package grupo12.practico.services.PushNotificationSender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushNotificationServiceBean Tests")
class PushNotificationServiceBeanTest {

    private PushNotificationServiceBean service;

    @BeforeEach
    void setUp() {
        service = new PushNotificationServiceBean();
    }

    @Test
    @DisplayName("sendPushNotification - Should throw RuntimeException")
    void sendPushNotification_ShouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> {
            service.sendPushNotification("Test Title", "Test Body");
        });
    }

    @Test
    @DisplayName("sendPushNotificationToToken - Should throw RuntimeException when Firebase not initialized")
    void sendPushNotificationToToken_ShouldThrowRuntimeExceptionWhenFirebaseNotInitialized() {
        // Firebase is not initialized in test environment, so this should fail
        assertThrows(RuntimeException.class, () -> {
            service.sendPushNotificationToToken("Test Title", "Test Body", "test-token");
        });
    }

    @Test
    @DisplayName("sendPushNotificationToTopic - Should throw RuntimeException when Firebase not initialized")
    void sendPushNotificationToTopic_ShouldThrowRuntimeExceptionWhenFirebaseNotInitialized() {
        // Firebase is not initialized in test environment, so this should fail
        assertThrows(RuntimeException.class, () -> {
            service.sendPushNotificationToTopic("Test Title", "Test Body", "test-topic");
        });
    }
}
