package grupo12.practico.messaging.Clinic;

import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import jakarta.validation.ValidationException;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = ClinicRegistrationMessaging.QUEUE_JNDI_NAME),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class ClinicRegistrationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(ClinicRegistrationMDB.class.getName());

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage textMessage)) {
            LOGGER.warning("Received unsupported JMS message type for clinic registration");
            return;
        }

        try {
            String payload = textMessage.getText();
            AddClinicDTO dto = ClinicRegistrationMessageMapper.fromMessage(payload);
            clinicService.addClinic(dto);
            LOGGER.info(() -> "Processed clinic registration for clinic " + dto.getName());
        } catch (ValidationException ex) {
            LOGGER.log(Level.WARNING, "Invalid clinic registration payload", ex);
        } catch (JMSException ex) {
            LOGGER.log(Level.SEVERE, "Failed to read JMS message", ex);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to process clinic registration", ex);
        }
    }
}
