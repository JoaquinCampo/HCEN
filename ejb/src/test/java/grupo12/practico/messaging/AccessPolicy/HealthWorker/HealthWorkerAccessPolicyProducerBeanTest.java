package grupo12.practico.messaging.AccessPolicy.HealthWorker;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
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
@DisplayName("HealthWorkerAccessPolicyProducerBean Tests")
class HealthWorkerAccessPolicyProducerBeanTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private Queue queue;

    private HealthWorkerAccessPolicyProducerBean producer;

    private AddHealthWorkerAccessPolicyDTO dto;

    @BeforeEach
    void setUp() throws Exception {
        producer = new HealthWorkerAccessPolicyProducerBean();

        Field jmsContextField = HealthWorkerAccessPolicyProducerBean.class.getDeclaredField("jmsContext");
        jmsContextField.setAccessible(true);
        jmsContextField.set(producer, jmsContext);

        Field queueField = HealthWorkerAccessPolicyProducerBean.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(producer, queue);

        dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setClinicName("Test Clinic");

        lenient().when(jmsContext.createProducer()).thenReturn(jmsProducer);
    }

    @Test
    @DisplayName("enqueue - Should successfully enqueue health worker access policy")
    void enqueue_ShouldSuccessfullyEnqueueHealthWorkerAccessPolicy() {
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
