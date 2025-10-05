package grupo12.practico.messaging.HealthWorker;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.validation.ValidationException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = HealthWorkerRegistrationMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class HealthWorkerRegistrationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(HealthWorkerRegistrationMDB.class.getName());

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for health worker registration");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddHealthWorkerDTO dto = HealthWorkerRegistrationMessageMapper.fromMessage(payload);
            healthWorkerService.add(dto);
            LOGGER.info(() -> "Processed health worker registration for document " + dto.getDocument());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid health worker registration payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process health worker registration", ex);
        }
    }
}
