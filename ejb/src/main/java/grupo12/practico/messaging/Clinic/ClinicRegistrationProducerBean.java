package grupo12.practico.messaging.Clinic;

import java.util.logging.Logger;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
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
@Local(ClinicRegistrationProducerLocal.class)
@Remote(ClinicRegistrationProducerRemote.class)
@JMSDestinationDefinition(name = ClinicRegistrationMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_add_clinic", resourceAdapter = "activemq-ra")
public class ClinicRegistrationProducerBean implements ClinicRegistrationProducerLocal,
        ClinicRegistrationProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(ClinicRegistrationProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = ClinicRegistrationMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddClinicDTO dto) {
        try {
            String payload = ClinicRegistrationMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued clinic registration request for clinic " + dto.getName());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue clinic registration request", ex);
        }
    }
}
