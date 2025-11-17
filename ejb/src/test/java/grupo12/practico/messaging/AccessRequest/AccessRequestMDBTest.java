package grupo12.practico.messaging.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessRequestMDB Tests")
class AccessRequestMDBTest {

    @Mock
    private AccessRequestServiceLocal accessRequestService;

    @Mock
    private TextMessage textMessage;

    @Mock
    private Message nonTextMessage;

    private AccessRequestMDB mdb;

    @BeforeEach
    void setUp() throws Exception {
        mdb = new AccessRequestMDB();

        Field serviceField = AccessRequestMDB.class.getDeclaredField("accessRequestService");
        serviceField.setAccessible(true);
        serviceField.set(mdb, accessRequestService);
    }

    @Test
    @DisplayName("onMessage - Should process valid text message successfully")
    void onMessage_ShouldProcessValidTextMessageSuccessfully() throws Exception {
        String payload = "12345678|87654321|Test Clinic|Cardiology,Neurology";
        when(textMessage.getText()).thenReturn(payload);

        mdb.onMessage(textMessage);

        verify(accessRequestService).createAccessRequest(any(AddAccessRequestDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should ignore non-text messages")
    void onMessage_ShouldIgnoreNonTextMessages() {
        mdb.onMessage(nonTextMessage);

        verifyNoInteractions(accessRequestService);
        verifyNoInteractions(nonTextMessage);
    }

    @Test
    @DisplayName("onMessage - Should handle validation exception from message mapper")
    void onMessage_ShouldHandleValidationExceptionFromMessageMapper() throws Exception {
        String invalidPayload = "invalid|payload|format";
        when(textMessage.getText()).thenReturn(invalidPayload);

        mdb.onMessage(textMessage);

        verify(accessRequestService, never()).createAccessRequest(any());
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should handle JMS exception when reading message")
    void onMessage_ShouldHandleJMSExceptionWhenReadingMessage() throws Exception {
        when(textMessage.getText()).thenThrow(new JMSException("JMS Error"));

        mdb.onMessage(textMessage);

        verify(accessRequestService, never()).createAccessRequest(any());
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should handle service exception")
    void onMessage_ShouldHandleServiceException() throws Exception {
        String payload = "12345678|87654321|Test Clinic|Cardiology";
        when(textMessage.getText()).thenReturn(payload);
        doThrow(new RuntimeException("Service error")).when(accessRequestService)
                .createAccessRequest(any(AddAccessRequestDTO.class));

        mdb.onMessage(textMessage);

        verify(accessRequestService).createAccessRequest(any(AddAccessRequestDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should handle null message gracefully")
    void onMessage_ShouldHandleNullMessageGracefully() {
        mdb.onMessage(null);

        verifyNoInteractions(accessRequestService);
    }

    @Test
    @DisplayName("onMessage - Should process message with all fields populated")
    void onMessage_ShouldProcessMessageWithAllFieldsPopulated() throws Exception {
        String payload = "12345678|87654321|Test Clinic|Cardiology,Neurology,Internal Medicine";
        when(textMessage.getText()).thenReturn(payload);

        mdb.onMessage(textMessage);

        verify(accessRequestService).createAccessRequest(any(AddAccessRequestDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should process message with minimal required fields")
    void onMessage_ShouldProcessMessageWithMinimalRequiredFields() throws Exception {
        String payload = "12345678|87654321|Test Clinic|";
        when(textMessage.getText()).thenReturn(payload);

        mdb.onMessage(textMessage);

        verify(accessRequestService).createAccessRequest(any(AddAccessRequestDTO.class));
        verify(textMessage).getText();
    }

    @Test
    @DisplayName("onMessage - Should process message with no specialty names")
    void onMessage_ShouldProcessMessageWithNoSpecialtyNames() throws Exception {
        String payload = "12345678|87654321|Test Clinic|";
        when(textMessage.getText()).thenReturn(payload);

        mdb.onMessage(textMessage);

        verify(accessRequestService).createAccessRequest(any(AddAccessRequestDTO.class));
        verify(textMessage).getText();
    }
}