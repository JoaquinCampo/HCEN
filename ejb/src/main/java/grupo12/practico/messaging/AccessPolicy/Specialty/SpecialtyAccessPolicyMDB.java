package grupo12.practico.messaging.AccessPolicy.Specialty;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.validation.ValidationException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = SpecialtyAccessPolicyMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class SpecialtyAccessPolicyMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(SpecialtyAccessPolicyMDB.class.getName());

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for specialty access policy");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddSpecialtyAccessPolicyDTO dto = SpecialtyAccessPolicyMessageMapper.fromMessage(payload);
            accessPolicyService.createSpecialtyAccessPolicy(dto);
            LOGGER.fine(() -> "Processed specialty access policy for health user " + dto.getHealthUserCi()
                    + " and specialty " + dto.getSpecialtyName());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid specialty access policy payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process specialty access policy", ex);
        }
    }
}

