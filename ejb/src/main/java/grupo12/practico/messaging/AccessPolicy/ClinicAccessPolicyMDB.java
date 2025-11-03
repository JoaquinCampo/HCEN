package grupo12.practico.messaging.AccessPolicy;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = ClinicAccessPolicyMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class ClinicAccessPolicyMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(ClinicAccessPolicyMDB.class.getName());

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for clinic access policy");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddClinicAccessPolicyDTO dto = ClinicAccessPolicyMessageMapper.fromMessage(payload);
            accessPolicyService.createClinicAccessPolicy(dto);
            LOGGER.fine(() -> "Processed clinic access policy for health user " + dto.getHealthUserId() + " and clinic " + dto.getClinicName());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid clinic access policy payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process clinic access policy", ex);
        }
    }
}

