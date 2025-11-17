package grupo12.practico.messaging.ClinicalDocument;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.services.ClinicalDocument.ClinicalDocumentServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.validation.ValidationException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = CreateDocumentMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class CreateDocumentMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(CreateDocumentMDB.class.getName());

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for create clinical document request");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddClinicalDocumentDTO dto = CreateDocumentMessageMapper.fromMessage(payload);
            clinicalDocumentService.createClinicalDocument(dto);
            LOGGER.fine(() -> "Processed create clinical document request for health user " + dto.getHealthUserCi()
                    + " and health worker " + dto.getHealthWorkerCi());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid create clinical document payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process create clinical document request", ex);
        }
    }
}

