package grupo12.practico.messaging.HealthUser;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.models.Gender;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Queue;
import jakarta.validation.ValidationException;
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
@DisplayName("HealthUserRegistrationProducerBean Tests")
class HealthUserRegistrationProducerBeanTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private Queue queue;

    private HealthUserRegistrationProducerBean producer;

    private AddHealthUserDTO addHealthUserDTO;

    @BeforeEach
    void setUp() throws Exception {
        producer = new HealthUserRegistrationProducerBean();

        // Use reflection to inject mocked dependencies
        Field jmsContextField = HealthUserRegistrationProducerBean.class.getDeclaredField("jmsContext");
        jmsContextField.setAccessible(true);
        jmsContextField.set(producer, jmsContext);

        Field queueField = HealthUserRegistrationProducerBean.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(producer, queue);

        // Setup test data
        addHealthUserDTO = new AddHealthUserDTO();
        addHealthUserDTO.setCi("12345678");
        addHealthUserDTO.setFirstName("John");
        addHealthUserDTO.setLastName("Doe");
        addHealthUserDTO.setGender(Gender.MALE);
        addHealthUserDTO.setEmail("john.doe@example.com");
        addHealthUserDTO.setPhone("+598123456789");
        addHealthUserDTO.setAddress("123 Main St");
        addHealthUserDTO.setDateOfBirth(LocalDate.of(1990, 1, 1));
        addHealthUserDTO.setClinicNames(new HashSet<>(Arrays.asList("Clinic A", "Clinic B")));

        lenient().when(jmsContext.createProducer()).thenReturn(jmsProducer);
    }

    @Test
    @DisplayName("enqueue - Should successfully enqueue health user registration")
    void enqueue_ShouldSuccessfullyEnqueueHealthUserRegistration() {
        producer.enqueue(addHealthUserDTO);

        verify(jmsContext).createProducer();
        verify(jmsProducer).send(eq(queue), anyString());
    }

    @Test
    @DisplayName("enqueue - Should throw NullPointerException for null DTO")
    void enqueue_ShouldThrowNullPointerExceptionForNullDto() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> producer.enqueue(null));

        assertEquals("health user dto must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("enqueue - Should throw ValidationException for invalid DTO")
    void enqueue_ShouldThrowValidationExceptionForInvalidDto() {
        AddHealthUserDTO invalidDto = new AddHealthUserDTO();
        // Leave required fields null

        ValidationException exception = assertThrows(ValidationException.class,
                () -> producer.enqueue(invalidDto));

        assertEquals("Field ci is required", exception.getMessage());
    }

    @Test
    @DisplayName("enqueue - Should throw IllegalStateException when JMS fails")
    void enqueue_ShouldThrowIllegalStateExceptionWhenJMSFails() {
        when(jmsProducer.send(any(), anyString())).thenThrow(new JMSRuntimeException("JMS error"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> producer.enqueue(addHealthUserDTO));

        assertEquals("Failed to enqueue health user registration request", exception.getMessage());
        assertInstanceOf(JMSRuntimeException.class, exception.getCause());
    }

    @Test
    @DisplayName("enqueue - Should handle DTO with null optional fields")
    void enqueue_ShouldHandleDtoWithNullOptionalFields() {
        AddHealthUserDTO dtoWithNulls = new AddHealthUserDTO();
        dtoWithNulls.setCi("12345678");
        dtoWithNulls.setFirstName("John");
        dtoWithNulls.setLastName("Doe");
        dtoWithNulls.setGender(Gender.MALE);
        dtoWithNulls.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dtoWithNulls.setClinicNames(new HashSet<>(Arrays.asList("Clinic A")));
        // Optional fields left null

        producer.enqueue(dtoWithNulls);

        verify(jmsContext).createProducer();
        verify(jmsProducer).send(eq(queue), anyString());
    }

    @Test
    @DisplayName("enqueue - Should handle DTO with empty clinic names")
    void enqueue_ShouldHandleDtoWithEmptyClinicNames() {
        AddHealthUserDTO dtoWithEmptyClinics = new AddHealthUserDTO();
        dtoWithEmptyClinics.setCi("12345678");
        dtoWithEmptyClinics.setFirstName("John");
        dtoWithEmptyClinics.setLastName("Doe");
        dtoWithEmptyClinics.setGender(Gender.MALE);
        dtoWithEmptyClinics.setEmail("john.doe@example.com");
        dtoWithEmptyClinics.setPhone("+598123456789");
        dtoWithEmptyClinics.setAddress("123 Main St");
        dtoWithEmptyClinics.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dtoWithEmptyClinics.setClinicNames(new HashSet<>()); // Empty set

        producer.enqueue(dtoWithEmptyClinics);

        verify(jmsContext).createProducer();
        verify(jmsProducer).send(eq(queue), anyString());
    }
}