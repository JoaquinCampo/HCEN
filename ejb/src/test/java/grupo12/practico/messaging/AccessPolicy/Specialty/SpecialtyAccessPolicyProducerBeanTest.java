package grupo12.practico.messaging.AccessPolicy.Specialty;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
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
@DisplayName("SpecialtyAccessPolicyProducerBean Tests")
class SpecialtyAccessPolicyProducerBeanTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private Queue queue;

    private SpecialtyAccessPolicyProducerBean producer;

    private AddSpecialtyAccessPolicyDTO dto;

    @BeforeEach
    void setUp() throws Exception {
        producer = new SpecialtyAccessPolicyProducerBean();

        Field jmsContextField = SpecialtyAccessPolicyProducerBean.class.getDeclaredField("jmsContext");
        jmsContextField.setAccessible(true);
        jmsContextField.set(producer, jmsContext);

        Field queueField = SpecialtyAccessPolicyProducerBean.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(producer, queue);

        dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi("12345678");
        dto.setSpecialtyName("Cardiology");

        lenient().when(jmsContext.createProducer()).thenReturn(jmsProducer);
    }

    @Test
    @DisplayName("enqueue - Should successfully enqueue specialty access policy")
    void enqueue_ShouldSuccessfullyEnqueueSpecialtyAccessPolicy() {
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
