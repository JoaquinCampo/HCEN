package grupo12.practico.messaging.ClinicalDocument.PresignedUrl;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = PresignedUrlMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class PresignedUrlMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(PresignedUrlMDB.class.getName());

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for presigned URL request");
            return;
        }

        try {
            String payload = textMessage.getText();
            PresignedUrlRequestDTO dto = PresignedUrlMessageMapper.fromMessage(payload);
            clinicalDocumentService.getPresignedUploadUrl(dto);
            LOGGER.fine(() -> "Processed presigned URL request for health user " + dto.getHealthUserCi()
                    + " and health worker " + dto.getHealthWorkerCi());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid presigned URL request payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process presigned URL request", ex);
        }
    }
}

