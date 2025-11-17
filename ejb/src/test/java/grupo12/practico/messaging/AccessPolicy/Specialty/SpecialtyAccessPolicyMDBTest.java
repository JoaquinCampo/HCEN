package grupo12.practico.messaging.AccessPolicy.Specialty;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

@ExtendWith(MockitoExtension.class)
class SpecialtyAccessPolicyMDBTest {

    @Mock
    private AccessPolicyServiceLocal accessPolicyService;

    @Mock
    private TextMessage textMessage;

    @Mock
    private Message nonTextMessage;

    private SpecialtyAccessPolicyMDB mdb;

    @BeforeEach
    void setUp() throws Exception {
        mdb = new SpecialtyAccessPolicyMDB();

        // Inject the mocked service using reflection
        Field serviceField = SpecialtyAccessPolicyMDB.class.getDeclaredField("accessPolicyService");
        serviceField.setAccessible(true);
        serviceField.set(mdb, accessPolicyService);
    }

    @Test
    void onMessage_ShouldProcessValidMessage() throws Exception {
        // Given
        String payload = "12345678|Cardiology|REQ-001";
        when(textMessage.getText()).thenReturn(payload);

        // When
        mdb.onMessage(textMessage);

        // Then
        verify(accessPolicyService).createSpecialtyAccessPolicy(argThat(dto -> {
            return "12345678".equals(dto.getHealthUserCi()) &&
                    "Cardiology".equals(dto.getSpecialtyName()) &&
                    "REQ-001".equals(dto.getAccessRequestId());
        }));
    }

    @Test
    void onMessage_ShouldProcessValidMessageWithNullAccessRequestId() throws Exception {
        // Given
        String payload = "12345678|Cardiology|";
        when(textMessage.getText()).thenReturn(payload);

        // When
        mdb.onMessage(textMessage);

        // Then
        verify(accessPolicyService).createSpecialtyAccessPolicy(argThat(dto -> {
            return "12345678".equals(dto.getHealthUserCi()) &&
                    "Cardiology".equals(dto.getSpecialtyName()) &&
                    dto.getAccessRequestId() == null;
        }));
    }

    @Test
    void onMessage_ShouldIgnoreNonTextMessage() throws Exception {
        // When
        mdb.onMessage(nonTextMessage);

        // Then
        verifyNoInteractions(accessPolicyService);
    }

    @Test
    void onMessage_ShouldHandleValidationExceptionFromMessageMapper() throws Exception {
        // Given
        String invalidPayload = "12345678"; // Missing fields
        when(textMessage.getText()).thenReturn(invalidPayload);

        // When
        mdb.onMessage(textMessage);

        // Then
        verifyNoInteractions(accessPolicyService);
    }

    @Test
    void onMessage_ShouldHandleJMSExceptionWhenReadingMessage() throws Exception {
        // Given
        when(textMessage.getText()).thenThrow(new JMSException("JMS Error"));

        // When
        mdb.onMessage(textMessage);

        // Then
        verifyNoInteractions(accessPolicyService);
    }

    @Test
    void onMessage_ShouldHandleServiceException() throws Exception {
        // Given
        String payload = "12345678|Cardiology|REQ-001";
        when(textMessage.getText()).thenReturn(payload);
        when(accessPolicyService.createSpecialtyAccessPolicy(any())).thenThrow(new RuntimeException("Service error"));

        // When
        mdb.onMessage(textMessage);

        // Then
        verify(accessPolicyService).createSpecialtyAccessPolicy(any());
    }

    @Test
    void onMessage_ShouldHandleNullMessage() throws Exception {
        // When
        mdb.onMessage(null);

        // Then
        verifyNoInteractions(accessPolicyService);
    }

    @Test
    void onMessage_ShouldHandleEmptyPayload() throws Exception {
        // Given
        when(textMessage.getText()).thenReturn("");

        // When
        mdb.onMessage(textMessage);

        // Then
        verifyNoInteractions(accessPolicyService);
    }

    @Test
    void onMessage_ShouldHandleInvalidPayloadFormat() throws Exception {
        // Given
        String invalidPayload = "invalid|format|with|extra|fields";
        when(textMessage.getText()).thenReturn(invalidPayload);

        // When
        mdb.onMessage(textMessage);

        // Then
        verifyNoInteractions(accessPolicyService);
    }
}