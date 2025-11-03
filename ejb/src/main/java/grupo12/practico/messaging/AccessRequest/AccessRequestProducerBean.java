package grupo12.practico.messaging.AccessRequest;

import java.util.logging.Logger;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
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
@Local(AccessRequestProducerLocal.class)
@Remote(AccessRequestProducerRemote.class)
@JMSDestinationDefinition(name = AccessRequestMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_add_access_request", resourceAdapter = "activemq-ra")
public class AccessRequestProducerBean implements AccessRequestProducerLocal,
        AccessRequestProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(AccessRequestProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = AccessRequestMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddAccessRequestDTO dto) {
        try {
            String payload = AccessRequestMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued access request for health user " + dto.getHealthUserCi() + " and health worker " + dto.getHealthWorkerCi());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue access request", ex);
        }
    }
}

