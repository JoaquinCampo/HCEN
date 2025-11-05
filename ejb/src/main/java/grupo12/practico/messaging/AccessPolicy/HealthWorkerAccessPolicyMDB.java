package grupo12.practico.messaging.AccessPolicy;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
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
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = HealthWorkerAccessPolicyMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class HealthWorkerAccessPolicyMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(HealthWorkerAccessPolicyMDB.class.getName());

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for health worker access policy");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddHealthWorkerAccessPolicyDTO dto = HealthWorkerAccessPolicyMessageMapper.fromMessage(payload);
            accessPolicyService.createHealthWorkerAccessPolicy(dto);
            LOGGER.fine(() -> "Processed health worker access policy for health user " + dto.getHealthUserId() + ", health worker " + dto.getHealthWorkerCi() + " and clinic " + dto.getClinicName());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid health worker access policy payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process health worker access policy", ex);
        }
    }
}

