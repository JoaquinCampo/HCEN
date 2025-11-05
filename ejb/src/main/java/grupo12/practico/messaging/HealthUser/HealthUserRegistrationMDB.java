package grupo12.practico.messaging.HealthUser;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = HealthUserRegistrationMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class HealthUserRegistrationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(HealthUserRegistrationMDB.class.getName());

    @EJB
    private HealthUserServiceLocal healthUserService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for health user registration");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddHealthUserDTO dto = HealthUserRegistrationMessageMapper.fromMessage(payload);
            healthUserService.create(dto);
            LOGGER.info(() -> "Processed health user registration for document " + dto.getCi());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid health user registration payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process health user registration", ex);
        }
    }
}
