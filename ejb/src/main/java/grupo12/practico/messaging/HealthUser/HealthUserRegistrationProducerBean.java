package grupo12.practico.messaging.HealthUser;

import java.util.logging.Logger;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
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
@Local(HealthUserRegistrationProducerLocal.class)
@Remote(HealthUserRegistrationProducerRemote.class)
@JMSDestinationDefinition(name = HealthUserRegistrationMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_alta_health_user", resourceAdapter = "activemq-ra")
public class HealthUserRegistrationProducerBean implements HealthUserRegistrationProducerLocal,
        HealthUserRegistrationProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(HealthUserRegistrationProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = HealthUserRegistrationMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddHealthUserDTO dto) {
        try {
            String payload = HealthUserRegistrationMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued health user registration request for document " + dto.getDocument());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue health user registration request", ex);
        }
    }
}
