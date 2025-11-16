package grupo12.practico.messaging.AccessPolicy.Specialty;

import java.util.logging.Logger;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
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
@Local(SpecialtyAccessPolicyProducerLocal.class)
@Remote(SpecialtyAccessPolicyProducerRemote.class)
@JMSDestinationDefinition(name = SpecialtyAccessPolicyMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_add_specialty_access_policy", resourceAdapter = "activemq-ra")
public class SpecialtyAccessPolicyProducerBean implements SpecialtyAccessPolicyProducerLocal,
        SpecialtyAccessPolicyProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(SpecialtyAccessPolicyProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = SpecialtyAccessPolicyMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddSpecialtyAccessPolicyDTO dto) {
        try {
            String payload = SpecialtyAccessPolicyMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued specialty access policy for health user " + dto.getHealthUserCi()
                    + " and specialty " + dto.getSpecialtyName());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue specialty access policy", ex);
        }
    }
}

