package grupo12.practico.messaging.AccessPolicy;

import java.util.logging.Logger;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
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
@Local(ClinicAccessPolicyProducerLocal.class)
@Remote(ClinicAccessPolicyProducerRemote.class)
@JMSDestinationDefinition(name = ClinicAccessPolicyMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_add_clinic_access_policy", resourceAdapter = "activemq-ra")
public class ClinicAccessPolicyProducerBean implements ClinicAccessPolicyProducerLocal,
        ClinicAccessPolicyProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(ClinicAccessPolicyProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = ClinicAccessPolicyMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddClinicAccessPolicyDTO dto) {
        try {
            String payload = ClinicAccessPolicyMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued clinic access policy for health user " + dto.getHealthUserCi() + " and clinic "
                    + dto.getClinicName());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue clinic access policy", ex);
        }
    }
}
