package grupo12.practico.messaging.ClinicalDocument.Chat;

import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import jakarta.annotation.Resource;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Queue;
import jakarta.jms.JMSRuntimeException;
import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

@Stateless
@Local(ChatProducerLocal.class)
@Remote(ChatProducerRemote.class)
@JMSDestinationDefinition(name = ChatMessaging.QUEUE_JNDI_NAME, interfaceName = "jakarta.jms.Queue", destinationName = "queue_chat_request", resourceAdapter = "activemq-ra")
public class ChatProducerBean implements ChatProducerLocal,
        ChatProducerRemote {

    private static final Logger LOGGER = Logger.getLogger(ChatProducerBean.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = ChatMessaging.QUEUE_JNDI_NAME)
    private Queue queue;

    @Override
    public void enqueue(ChatRequestDTO dto) {
        try {
            String payload = ChatMessageMapper.toMessage(dto);
            jmsContext.createProducer().send(queue, payload);
            LOGGER.fine(() -> "Queued chat request for health user " + dto.getHealthUserCi()
                    + " and document " + dto.getDocumentId());
        } catch (ValidationException ex) {
            throw ex;
        } catch (JMSRuntimeException ex) {
            throw new IllegalStateException("Failed to enqueue chat request", ex);
        }
    }
}

