package grupo12.practico.messaging.HealthWorker;

import java.util.logging.Logger;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
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
@Local(HealthWorkerRegistrationProducerLocal.class)
@Remote(HealthWorkerRegistrationProducerRemote.class)
@JMSDestinationDefinition(name = HealthWorkerRegistrationMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_alta_health_worker", resourceAdapter = "activemq-ra")
public class HealthWorkerRegistrationProducerBean implements HealthWorkerRegistrationProducerLocal,
        HealthWorkerRegistrationProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(HealthWorkerRegistrationProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = HealthWorkerRegistrationMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddHealthWorkerDTO dto) {
        try {
            String payload = HealthWorkerRegistrationMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued health worker registration request for document " + dto.getDocument());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue health worker registration request", ex);
        }
    }
}
