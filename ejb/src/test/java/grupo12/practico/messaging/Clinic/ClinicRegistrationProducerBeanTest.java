package grupo12.practico.messaging.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSProducer;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicRegistrationProducerBean Tests")
class ClinicRegistrationProducerBeanTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private Queue queue;

    private ClinicRegistrationProducerBean producer;

    private AddClinicDTO addClinicDTO;
    private ClinicAdminDTO clinicAdminDTO;

    @BeforeEach
    void setUp() throws Exception {
        producer = new ClinicRegistrationProducerBean();

        // Use reflection to inject mocked dependencies
        Field jmsContextField = ClinicRegistrationProducerBean.class.getDeclaredField("jmsContext");
        jmsContextField.setAccessible(true);
        jmsContextField.set(producer, jmsContext);

        Field queueField = ClinicRegistrationProducerBean.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(producer, queue);

        // Setup test data
        clinicAdminDTO = new ClinicAdminDTO();
        clinicAdminDTO.setCi("12345678");
        clinicAdminDTO.setFirstName("Admin");
        clinicAdminDTO.setLastName("User");
        clinicAdminDTO.setEmail("admin@clinic.com");
        clinicAdminDTO.setPhone("123456789");
        clinicAdminDTO.setAddress("123 Admin St");
        clinicAdminDTO.setDateOfBirth(LocalDate.of(1980, 1, 1));

        addClinicDTO = new AddClinicDTO();
        addClinicDTO.setName("Test Clinic");
        addClinicDTO.setEmail("clinic@test.com");
        addClinicDTO.setPhone("987654321");
        addClinicDTO.setAddress("456 Clinic Ave");
        addClinicDTO.setProviderName("Test Provider");
        addClinicDTO.setClinicAdmin(clinicAdminDTO);

        lenient().when(jmsContext.createProducer()).thenReturn(jmsProducer);
    }

    @Test
    @DisplayName("enqueue - Should successfully enqueue clinic registration")
    void enqueue_ShouldSuccessfullyEnqueueClinicRegistration() {
        producer.enqueue(addClinicDTO);

        verify(jmsContext).createProducer();
        verify(jmsProducer).send(eq(queue), anyString());
    }

    @Test
    @DisplayName("enqueue - Should throw IllegalStateException when JMS fails")
    void enqueue_ShouldThrowIllegalStateExceptionWhenJMSFails() {
        when(jmsContext.createProducer()).thenThrow(new JMSRuntimeException("JMS Error"));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> producer.enqueue(addClinicDTO));

        assertEquals("Failed to enqueue clinic registration request", exception.getMessage());
        assertTrue(exception.getCause() instanceof JMSRuntimeException);
    }

    @Test
    @DisplayName("enqueue - Should throw ValidationException when message mapping fails")
    void enqueue_ShouldThrowValidationExceptionWhenMessageMappingFails() {
        addClinicDTO.setName("Clinic|With|Pipe");

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> producer.enqueue(addClinicDTO));

        assertTrue(exception.getMessage().contains("must not contain '|'"));
        // Note: JMS mocks are not set up for this test since it fails before reaching
        // JMS code
    }

    @Test
    @DisplayName("enqueue - Should throw ValidationException when clinic admin is null")
    void enqueue_ShouldThrowValidationExceptionWhenClinicAdminIsNull() {
        addClinicDTO.setClinicAdmin(null);

        jakarta.validation.ValidationException exception = assertThrows(
                jakarta.validation.ValidationException.class,
                () -> producer.enqueue(addClinicDTO));

        assertTrue(exception.getMessage().contains("clinicAdmin.ci is required"));
    }

    @Test
    @DisplayName("enqueue - Should handle clinic admin with null optional fields")
    void enqueue_ShouldHandleClinicAdminWithNullOptionalFields() {
        clinicAdminDTO.setPhone(null);
        clinicAdminDTO.setAddress(null);
        clinicAdminDTO.setDateOfBirth(null);

        producer.enqueue(addClinicDTO);

        verify(jmsContext).createProducer();
        verify(jmsProducer).send(eq(queue), anyString());
    }
}