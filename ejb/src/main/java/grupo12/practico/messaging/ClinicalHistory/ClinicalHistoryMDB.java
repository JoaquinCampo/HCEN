package grupo12.practico.messaging.ClinicalHistory;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.validation.ValidationException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = ClinicalHistoryMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class ClinicalHistoryMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(ClinicalHistoryMDB.class.getName());

    @EJB
    private HealthUserServiceLocal healthUserService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for clinical history request");
            return;
        }

        try {
            String payload = textMessage.getText();
            ClinicalHistoryRequestDTO dto = ClinicalHistoryMessageMapper.fromMessage(payload);
            healthUserService.findHealthUserClinicalHistory(dto);
            LOGGER.fine(() -> "Processed clinical history request for health user " + dto.getHealthUserCi());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid clinical history request payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process clinical history request", ex);
        }
    }
}

