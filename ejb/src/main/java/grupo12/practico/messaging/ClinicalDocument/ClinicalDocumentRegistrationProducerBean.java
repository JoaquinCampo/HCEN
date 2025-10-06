package grupo12.practico.messaging.ClinicalDocument;

import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
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
@Local(ClinicalDocumentRegistrationProducerLocal.class)
@Remote(ClinicalDocumentRegistrationProducerRemote.class)
@JMSDestinationDefinition(name = ClinicalDocumentRegistrationMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_alta_clinical_document", resourceAdapter = "activemq-ra")
public class ClinicalDocumentRegistrationProducerBean implements ClinicalDocumentRegistrationProducerLocal,
        ClinicalDocumentRegistrationProducerRemote {

    private static final Logger LOGGER = Logger
            .getLogger(ClinicalDocumentRegistrationProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = ClinicalDocumentRegistrationMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddClinicalDocumentDTO dto) {
        try {
            String payload = ClinicalDocumentRegistrationMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued clinical document creation request with title '" + dto.getTitle() + "'");
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue clinical document creation request", ex);
        }
    }
}
