package grupo12.practico.messaging.ClinicalDocument.Chat;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = ChatMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class ChatMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(ChatMDB.class.getName());

    @EJB
    private ClinicalDocumentServiceLocal clinicalDocumentService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for chat request");
            return;
        }

        try {
            String payload = textMessage.getText();
            ChatRequestDTO dto = ChatMessageMapper.fromMessage(payload);
            clinicalDocumentService.chat(dto);
            LOGGER.fine(() -> "Processed chat request for health user " + dto.getHealthUserCi()
                    + " and document " + dto.getDocumentId());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid chat request payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process chat request", ex);
        }
    }
}

