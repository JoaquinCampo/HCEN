package grupo12.practico.messaging.AccessPolicy.Clinic;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicAccessPolicyProducerBean Tests")
class ClinicAccessPolicyProducerBeanTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private Queue queue;

    private ClinicAccessPolicyProducerBean producer;

    private AddClinicAccessPolicyDTO dto;

    @BeforeEach
    void setUp() throws Exception {
        producer = new ClinicAccessPolicyProducerBean();

        Field jmsContextField = ClinicAccessPolicyProducerBean.class.getDeclaredField("jmsContext");
        jmsContextField.setAccessible(true);
        jmsContextField.set(producer, jmsContext);

        Field queueField = ClinicAccessPolicyProducerBean.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(producer, queue);

        dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setClinicName("Test Clinic");

        lenient().when(jmsContext.createProducer()).thenReturn(jmsProducer);
    }

    @Test
    @DisplayName("enqueue - Should successfully enqueue clinic access policy")
    void enqueue_ShouldSuccessfullyEnqueueClinicAccessPolicy() {
        producer.enqueue(dto);

        verify(jmsContext).createProducer();
        verify(jmsProducer).send(eq(queue), anyString());
    }

    @Test
    @DisplayName("enqueue - Should throw IllegalStateException on JMSRuntimeException")
    void enqueue_ShouldThrowIllegalStateExceptionOnJMSRuntimeException() {
        when(jmsContext.createProducer()).thenThrow(new JMSRuntimeException("JMS Error"));

        assertThrows(IllegalStateException.class, () -> {
            producer.enqueue(dto);
        });
    }
}
