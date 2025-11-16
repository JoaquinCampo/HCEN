package grupo12.practico.messaging.ClinicalDocument.CreateDocument;

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
@Local(CreateDocumentProducerLocal.class)
@Remote(CreateDocumentProducerRemote.class)
@JMSDestinationDefinition(name = CreateDocumentMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_create_clinical_document", resourceAdapter = "activemq-ra")
public class CreateDocumentProducerBean implements CreateDocumentProducerLocal,
        CreateDocumentProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(CreateDocumentProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = CreateDocumentMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(AddClinicalDocumentDTO dto) {
        try {
            String payload = CreateDocumentMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued create clinical document request for health user " + dto.getHealthUserCi()
                    + " and health worker " + dto.getHealthWorkerCi());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue create clinical document request", ex);
        }
    }
}

