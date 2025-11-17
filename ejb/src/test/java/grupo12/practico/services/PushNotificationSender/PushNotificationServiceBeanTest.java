package grupo12.practico.services.PushNotificationSender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushNotificationServiceBean Tests")
class PushNotificationServiceBeanTest {

    private PushNotificationServiceBean service;

    @BeforeEach
    void setUp() {
        service = new PushNotificationServiceBean();
    }

    @Test
    @DisplayName("sendPushNotification - Should throw UnsupportedOperationException when Firebase is initialized")
    void sendPushNotification_ShouldThrowUnsupportedOperationExceptionWhenFirebaseInitialized() {
        // Mock Firebase to be already initialized
        try (MockedStatic<com.google.firebase.FirebaseApp> firebaseAppMock = mockStatic(
                com.google.firebase.FirebaseApp.class)) {
            firebaseAppMock.when(com.google.firebase.FirebaseApp::getApps)
                    .thenReturn(java.util.Arrays.asList(mock(com.google.firebase.FirebaseApp.class)));

            UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
                service.sendPushNotification("Test Title", "Test Body");
            });
            assertTrue(exception.getMessage().contains("deprecated"));
        }
    }

    @Test
    @DisplayName("sendPushNotification - Should throw RuntimeException when Firebase not initialized")
    void sendPushNotification_ShouldThrowRuntimeExceptionWhenFirebaseNotInitialized() {
        // Firebase is not initialized in test environment, so this should fail with
        // file not found
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            service.sendPushNotification("Test Title", "Test Body");
        });
        assertTrue(exception.getMessage().contains("Didn't find the Firebase PK file"));
    }

    @Test
    @DisplayName("sendPushNotificationToToken - Should send notification successfully")
    void sendPushNotificationToToken_ShouldSendNotificationSuccessfully() throws Exception {
        // Mock Firebase components
        try (MockedStatic<com.google.firebase.FirebaseApp> firebaseAppMock = mockStatic(
                com.google.firebase.FirebaseApp.class);
                MockedStatic<com.google.firebase.messaging.FirebaseMessaging> firebaseMessagingMock = mockStatic(
                        com.google.firebase.messaging.FirebaseMessaging.class)) {

            // Setup Firebase mocks
            firebaseAppMock.when(com.google.firebase.FirebaseApp::getApps)
                    .thenReturn(java.util.Arrays.asList(mock(com.google.firebase.FirebaseApp.class)));
            com.google.firebase.messaging.FirebaseMessaging mockMessaging = mock(
                    com.google.firebase.messaging.FirebaseMessaging.class);
            firebaseMessagingMock.when(com.google.firebase.messaging.FirebaseMessaging::getInstance)
                    .thenReturn(mockMessaging);

            // Mock successful send
            when(mockMessaging.send(any(com.google.firebase.messaging.Message.class))).thenReturn("message-id-123");

            // Call the method
            assertDoesNotThrow(() -> service.sendPushNotificationToToken("Test Title", "Test Body", "test-token"));

            // Verify send was called
            verify(mockMessaging, times(1)).send(any(com.google.firebase.messaging.Message.class));
        }
    }

    @Test
    @DisplayName("sendPushNotificationToToken - Should throw RuntimeException on FirebaseMessagingException")
    void sendPushNotificationToToken_ShouldThrowRuntimeExceptionOnFirebaseMessagingException() throws Exception {
        // Mock Firebase components
        try (MockedStatic<com.google.firebase.FirebaseApp> firebaseAppMock = mockStatic(
                com.google.firebase.FirebaseApp.class);
                MockedStatic<com.google.firebase.messaging.FirebaseMessaging> firebaseMessagingMock = mockStatic(
                        com.google.firebase.messaging.FirebaseMessaging.class)) {

            // Setup Firebase mocks
            firebaseAppMock.when(com.google.firebase.FirebaseApp::getApps)
                    .thenReturn(java.util.Arrays.asList(mock(com.google.firebase.FirebaseApp.class)));
            com.google.firebase.messaging.FirebaseMessaging mockMessaging = mock(
                    com.google.firebase.messaging.FirebaseMessaging.class);
            firebaseMessagingMock.when(com.google.firebase.messaging.FirebaseMessaging::getInstance)
                    .thenReturn(mockMessaging);

            // Mock FirebaseMessagingException
            com.google.firebase.messaging.FirebaseMessagingException mockException = mock(
                    com.google.firebase.messaging.FirebaseMessagingException.class);
            when(mockMessaging.send(any(com.google.firebase.messaging.Message.class))).thenThrow(mockException);

            // Call the method and verify exception
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> service.sendPushNotificationToToken("Test Title", "Test Body", "test-token"));
            assertTrue(exception.getMessage().contains("Failed to send push notification to token"));
            assertEquals(mockException, exception.getCause());
        }
    }

    @Test
    @DisplayName("sendPushNotificationToTopic - Should send notification successfully")
    void sendPushNotificationToTopic_ShouldSendNotificationSuccessfully() throws Exception {
        // Mock Firebase components
        try (MockedStatic<com.google.firebase.FirebaseApp> firebaseAppMock = mockStatic(
                com.google.firebase.FirebaseApp.class);
                MockedStatic<com.google.firebase.messaging.FirebaseMessaging> firebaseMessagingMock = mockStatic(
                        com.google.firebase.messaging.FirebaseMessaging.class)) {

            // Setup Firebase mocks
            firebaseAppMock.when(com.google.firebase.FirebaseApp::getApps)
                    .thenReturn(java.util.Arrays.asList(mock(com.google.firebase.FirebaseApp.class)));
            com.google.firebase.messaging.FirebaseMessaging mockMessaging = mock(
                    com.google.firebase.messaging.FirebaseMessaging.class);
            firebaseMessagingMock.when(com.google.firebase.messaging.FirebaseMessaging::getInstance)
                    .thenReturn(mockMessaging);

            // Mock successful send
            when(mockMessaging.send(any(com.google.firebase.messaging.Message.class))).thenReturn("message-id-456");

            // Call the method
            assertDoesNotThrow(() -> service.sendPushNotificationToTopic("Test Title", "Test Body", "test-topic"));

            // Verify send was called
            verify(mockMessaging, times(1)).send(any(com.google.firebase.messaging.Message.class));
        }
    }

    @Test
    @DisplayName("sendPushNotificationToTopic - Should throw RuntimeException on FirebaseMessagingException")
    void sendPushNotificationToTopic_ShouldThrowRuntimeExceptionOnFirebaseMessagingException() throws Exception {
        // Mock Firebase components
        try (MockedStatic<com.google.firebase.FirebaseApp> firebaseAppMock = mockStatic(
                com.google.firebase.FirebaseApp.class);
                MockedStatic<com.google.firebase.messaging.FirebaseMessaging> firebaseMessagingMock = mockStatic(
                        com.google.firebase.messaging.FirebaseMessaging.class)) {

            // Setup Firebase mocks
            firebaseAppMock.when(com.google.firebase.FirebaseApp::getApps)
                    .thenReturn(java.util.Arrays.asList(mock(com.google.firebase.FirebaseApp.class)));
            com.google.firebase.messaging.FirebaseMessaging mockMessaging = mock(
                    com.google.firebase.messaging.FirebaseMessaging.class);
            firebaseMessagingMock.when(com.google.firebase.messaging.FirebaseMessaging::getInstance)
                    .thenReturn(mockMessaging);

            // Mock FirebaseMessagingException
            com.google.firebase.messaging.FirebaseMessagingException mockException = mock(
                    com.google.firebase.messaging.FirebaseMessagingException.class);
            when(mockMessaging.send(any(com.google.firebase.messaging.Message.class))).thenThrow(mockException);

            // Call the method and verify exception
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> service.sendPushNotificationToTopic("Test Title", "Test Body", "test-topic"));
            assertTrue(exception.getMessage().contains("Failed to send push notification to topic"));
            assertEquals(mockException, exception.getCause());
        }
    }

    @Test
    @DisplayName("sendPushNotificationToToken - Should handle FirebaseMessagingException gracefully")
    void sendPushNotificationToToken_ShouldHandleFirebaseMessagingExceptionGracefully() throws Exception {
        // Mock Firebase components
        try (MockedStatic<com.google.firebase.FirebaseApp> firebaseAppMock = mockStatic(
                com.google.firebase.FirebaseApp.class);
                MockedStatic<com.google.firebase.messaging.FirebaseMessaging> firebaseMessagingMock = mockStatic(
                        com.google.firebase.messaging.FirebaseMessaging.class)) {

            // Setup Firebase mocks
            firebaseAppMock.when(com.google.firebase.FirebaseApp::getApps)
                    .thenReturn(java.util.Arrays.asList(mock(com.google.firebase.FirebaseApp.class)));
            com.google.firebase.messaging.FirebaseMessaging mockMessaging = mock(
                    com.google.firebase.messaging.FirebaseMessaging.class);
            firebaseMessagingMock.when(com.google.firebase.messaging.FirebaseMessaging::getInstance)
                    .thenReturn(mockMessaging);

            // Mock FirebaseMessagingException
            com.google.firebase.messaging.FirebaseMessagingException mockException = mock(
                    com.google.firebase.messaging.FirebaseMessagingException.class);
            when(mockMessaging.send(any(com.google.firebase.messaging.Message.class))).thenThrow(mockException);

            // Call the method - should throw RuntimeException
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> service.sendPushNotificationToToken("Test Title", "Test Body", "invalid-token"));
            assertTrue(exception.getMessage().contains("Failed to send push notification to token"));
        }
    }
}
