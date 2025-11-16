package grupo12.practico.messaging.ClinicalHistory;

import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Queue;
import jakarta.jms.JMSRuntimeException;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

@Stateless
@Local(ClinicalHistoryProducerLocal.class)
@Remote(ClinicalHistoryProducerRemote.class)
@JMSDestinationDefinition(name = ClinicalHistoryMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_clinical_history_request", resourceAdapter = "activemq-ra")
public class ClinicalHistoryProducerBean implements ClinicalHistoryProducerLocal,
        ClinicalHistoryProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(ClinicalHistoryProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = ClinicalHistoryMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(ClinicalHistoryRequestDTO dto) {
        try {
            String payload = ClinicalHistoryMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued clinical history request for health user " + dto.getHealthUserCi());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue clinical history request", ex);
        }
    }
}

