package grupo12.practico.messaging.HealthUser;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.Gender;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("HealthUserRegistrationMDB Tests")
class HealthUserRegistrationMDBTest {

    @Mock
    private HealthUserServiceLocal healthUserService;

    @Mock
    private TextMessage textMessage;

    private HealthUserRegistrationMDB mdb;

    private AddHealthUserDTO validDto;
    private HealthUserDTO createdHealthUser;

    @BeforeEach
    void setUp() throws Exception {
        mdb = new HealthUserRegistrationMDB();

        // Use reflection to inject mocked service
        Field serviceField = HealthUserRegistrationMDB.class.getDeclaredField("healthUserService");
        serviceField.setAccessible(true);
        serviceField.set(mdb, healthUserService);

        // Setup test data
        validDto = new AddHealthUserDTO();
        validDto.setCi("12345678");
        validDto.setFirstName("John");
        validDto.setLastName("Doe");
        validDto.setGender(Gender.MALE);
        validDto.setEmail("john.doe@example.com");
        validDto.setPhone("+598123456789");
        validDto.setAddress("123 Main St");
        validDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        validDto.setClinicNames(new HashSet<>(Arrays.asList("Clinic A", "Clinic B")));

        createdHealthUser = new HealthUserDTO();
        createdHealthUser.setId("health-user-id");
        createdHealthUser.setCi("12345678");
        createdHealthUser.setFirstName("John");
        createdHealthUser.setLastName("Doe");
        createdHealthUser.setGender(Gender.MALE);
        createdHealthUser.setEmail("john.doe@example.com");
        createdHealthUser.setPhone("+598123456789");
        createdHealthUser.setAddress("123 Main St");
        createdHealthUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("onMessage - Should successfully process valid health user registration")
    void onMessage_ShouldSuccessfullyProcessValidHealthUserRegistration() throws JMSException {
        String validMessage = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|Clinic A,Clinic B";
        when(textMessage.getText()).thenReturn(validMessage);
        when(healthUserService.createHealthUser(any(AddHealthUserDTO.class))).thenReturn(createdHealthUser);

        mdb.onMessage(textMessage);

        verify(textMessage).getText();
        verify(healthUserService).createHealthUser(any(AddHealthUserDTO.class));
    }

    @Test
    @DisplayName("onMessage - Should handle message with empty optional fields")
    void onMessage_ShouldHandleMessageWithEmptyOptionalFields() throws JMSException {
        String messageWithNulls = "12345678|John|Doe|MALE||||1990-01-01|Clinic A";
        when(textMessage.getText()).thenReturn(messageWithNulls);
        when(healthUserService.createHealthUser(any(AddHealthUserDTO.class))).thenReturn(createdHealthUser);

        mdb.onMessage(textMessage);

        verify(textMessage).getText();
        verify(healthUserService).createHealthUser(any(AddHealthUserDTO.class));
    }

    @Test
    @DisplayName("onMessage - Should ignore non-TextMessage")
    void onMessage_ShouldIgnoreNonTextMessage() {
        mdb.onMessage(mock(jakarta.jms.Message.class));

        verifyNoInteractions(textMessage);
        verifyNoInteractions(healthUserService);
    }

    @Test
    @DisplayName("onMessage - Should handle ValidationException from message parsing")
    void onMessage_ShouldHandleValidationExceptionFromMessageParsing() throws JMSException {
        String invalidMessage = "invalid|message|with|wrong|field|count";
        when(textMessage.getText()).thenReturn(invalidMessage);

        // Should not throw exception, just log warning
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(textMessage).getText();
        verifyNoInteractions(healthUserService);
    }

    @Test
    @DisplayName("onMessage - Should handle JMSException")
    void onMessage_ShouldHandleJMSException() throws JMSException {
        when(textMessage.getText()).thenThrow(new JMSException("JMS error"));

        // Should not throw exception, just log severe
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(textMessage).getText();
        verifyNoInteractions(healthUserService);
    }

    @Test
    @DisplayName("onMessage - Should handle service exception")
    void onMessage_ShouldHandleServiceException() throws JMSException {
        String validMessage = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|Clinic A,Clinic B";
        when(textMessage.getText()).thenReturn(validMessage);
        when(healthUserService.createHealthUser(any(AddHealthUserDTO.class)))
                .thenThrow(new RuntimeException("Service error"));

        // Should not throw exception, just log severe
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(textMessage).getText();
        verify(healthUserService).createHealthUser(any(AddHealthUserDTO.class));
    }

    @Test
    @DisplayName("onMessage - Should handle null message payload")
    void onMessage_ShouldHandleNullMessagePayload() throws JMSException {
        when(textMessage.getText()).thenReturn(null);

        // Should not throw exception, just log warning
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(textMessage).getText();
        verifyNoInteractions(healthUserService);
    }

    @Test
    @DisplayName("onMessage - Should handle empty message payload")
    void onMessage_ShouldHandleEmptyMessagePayload() throws JMSException {
        when(textMessage.getText()).thenReturn("");

        // Should not throw exception, just log warning
        assertDoesNotThrow(() -> mdb.onMessage(textMessage));

        verify(textMessage).getText();
        verifyNoInteractions(healthUserService);
    }

    @Test
    @DisplayName("onMessage - Should handle message with empty clinic names")
    void onMessage_ShouldHandleMessageWithEmptyClinicNames() throws JMSException {
        String messageWithEmptyClinics = "12345678|John|Doe|MALE|john.doe@example.com|+598123456789|123 Main St|1990-01-01|";
        when(textMessage.getText()).thenReturn(messageWithEmptyClinics);
        when(healthUserService.createHealthUser(any(AddHealthUserDTO.class))).thenReturn(createdHealthUser);

        mdb.onMessage(textMessage);

        verify(textMessage).getText();
        verify(healthUserService).createHealthUser(any(AddHealthUserDTO.class));
    }
}