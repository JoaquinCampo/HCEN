package grupo12.practico.messaging.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
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
@DisplayName("CreateDocumentProducerBean Tests")
class CreateDocumentProducerBeanTest {

    @Mock
    private JMSContext jmsContext;

    @Mock
    private JMSProducer jmsProducer;

    @Mock
    private Queue queue;

    private CreateDocumentProducerBean producer;

    private AddClinicalDocumentDTO dto;

    @BeforeEach
    void setUp() throws Exception {
        producer = new CreateDocumentProducerBean();

        Field jmsContextField = CreateDocumentProducerBean.class.getDeclaredField("jmsContext");
        jmsContextField.setAccessible(true);
        jmsContextField.set(producer, jmsContext);

        Field queueField = CreateDocumentProducerBean.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(producer, queue);

        dto = new AddClinicalDocumentDTO();
        dto.setHealthUserCi("12345678");
        dto.setHealthWorkerCi("87654321");
        dto.setTitle("Test Document");
        dto.setClinicName("Test Clinic");

        lenient().when(jmsContext.createProducer()).thenReturn(jmsProducer);
    }

    @Test
    @DisplayName("enqueue - Should successfully enqueue create document request")
    void enqueue_ShouldSuccessfullyEnqueueCreateDocumentRequest() {
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
