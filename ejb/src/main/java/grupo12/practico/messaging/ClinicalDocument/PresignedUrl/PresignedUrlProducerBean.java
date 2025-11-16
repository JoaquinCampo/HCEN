package grupo12.practico.messaging.ClinicalDocument.PresignedUrl;

import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
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
@Local(PresignedUrlProducerLocal.class)
@Remote(PresignedUrlProducerRemote.class)
@JMSDestinationDefinition(name = PresignedUrlMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_presigned_url_request", resourceAdapter = "activemq-ra")
public class PresignedUrlProducerBean implements PresignedUrlProducerLocal,
        PresignedUrlProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(PresignedUrlProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = PresignedUrlMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(PresignedUrlRequestDTO dto) {
        try {
            String payload = PresignedUrlMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued presigned URL request for health user " + dto.getHealthUserCi()
                    + " and health worker " + dto.getHealthWorkerCi());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue presigned URL request", ex);
        }
    }
}

