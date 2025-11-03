package grupo12.practico.messaging.AccessRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.validation.ValidationException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = AccessRequestMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class AccessRequestMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(AccessRequestMDB.class.getName());

    @EJB
    private AccessRequestServiceLocal accessRequestService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for access request");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddAccessRequestDTO dto = AccessRequestMessageMapper.fromMessage(payload);
            accessRequestService.create(dto);
            LOGGER.fine(() -> "Processed access request for health user " + dto.getHealthUserCi() + " and health worker " + dto.getHealthWorkerCi());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid access request payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process access request", ex);
        }
    }
}

