package grupo12.practico.messaging.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicRegistrationMDB Tests")
class ClinicRegistrationMDBTest {

    @Mock
    private ClinicServiceLocal clinicService;

    @Mock
    private TextMessage textMessage;

    private ClinicRegistrationMDB mdb;

    @BeforeEach
    void setUp() throws Exception {
        mdb = new ClinicRegistrationMDB();

        // Use reflection to inject mocked dependencies
        Field clinicServiceField = ClinicRegistrationMDB.class.getDeclaredField("clinicService");
        clinicServiceField.setAccessible(true);
        clinicServiceField.set(mdb, clinicService);
    }

    @Test
    @DisplayName("onMessage - Should successfully process clinic registration message")
    void onMessage_ShouldSuccessfullyProcessClinicRegistrationMessage() throws Exception {
        String payload = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|123456789|123 Admin St|1980-01-01|Test Provider";

        when(textMessage.getText()).thenReturn(payload);

        mdb.onMessage(textMessage);

        verify(clinicService).createClinic(any(AddClinicDTO.class));
    }

    @Test
    @DisplayName("onMessage - Should ignore non-text messages")
    void onMessage_ShouldIgnoreNonTextMessages() {
        Message nonTextMessage = mock(Message.class);

        mdb.onMessage(nonTextMessage);

        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("onMessage - Should handle ValidationException from message mapping")
    void onMessage_ShouldHandleValidationExceptionFromMessageMapping() throws Exception {
        String invalidPayload = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|||||||"; // Missing required
                                                                                               // fields

        when(textMessage.getText()).thenReturn(invalidPayload);

        // Should not throw exception, just log warning
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("onMessage - Should handle JMSException")
    void onMessage_ShouldHandleJMSException() throws Exception {
        when(textMessage.getText()).thenThrow(new JMSException("JMS Error"));

        // Should not throw exception, just log error
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("onMessage - Should handle general exceptions from clinic service")
    void onMessage_ShouldHandleGeneralExceptionsFromClinicService() throws Exception {
        String payload = "Test Clinic|clinic@test.com|987654321|456 Clinic Ave|12345678|Admin|User|admin@clinic.com|123456789|123 Admin St|1980-01-01|Test Provider";

        when(textMessage.getText()).thenReturn(payload);
        when(clinicService.createClinic(any(AddClinicDTO.class))).thenThrow(new RuntimeException("Service error"));

        // Should not throw exception, just log error
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(clinicService).createClinic(any(AddClinicDTO.class));
    }

    @Test
    @DisplayName("onMessage - Should handle null message payload")
    void onMessage_ShouldHandleNullMessagePayload() throws Exception {
        when(textMessage.getText()).thenReturn(null);

        // Should not throw exception, just log warning
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verifyNoInteractions(clinicService);
    }

    @Test
    @DisplayName("onMessage - Should handle empty message payload")
    void onMessage_ShouldHandleEmptyMessagePayload() throws Exception {
        when(textMessage.getText()).thenReturn("");

        // Should not throw exception, just log warning
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verifyNoInteractions(clinicService);
    }
}