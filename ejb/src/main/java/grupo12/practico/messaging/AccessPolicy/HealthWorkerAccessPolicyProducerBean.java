package grupo12.practico.messaging.AccessPolicy;

import java.util.logging.Logger;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
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
@Local(HealthWorkerAccessPolicyProducerLocal.class)
@Remote(HealthWorkerAccessPolicyProducerRemote.class)
@JMSDestinationDefinition(name = HealthWorkerAccessPolicyMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_add_health_worker_access_policy", resourceAdapter = "activemq-ra")
public class HealthWorkerAccessPolicyProducerBean implements HealthWorkerAccessPolicyProducerLocal,
        HealthWorkerAccessPolicyProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(HealthWorkerAccessPolicyProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = HealthWorkerAccessPolicyMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddHealthWorkerAccessPolicyDTO dto) {
        try {
            String payload = HealthWorkerAccessPolicyMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued health worker access policy for health user " + dto.getHealthUserId() + ", health worker " + dto.getHealthWorkerCi() + " and clinic " + dto.getClinicName());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue health worker access policy", ex);
        }
    }
}

