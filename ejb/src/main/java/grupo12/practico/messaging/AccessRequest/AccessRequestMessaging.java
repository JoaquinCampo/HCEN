package grupo12.practico.messaging.AccessRequest;

/**
 * Centralizes JMS configuration details for the access request flow.
 */
public final class AccessRequestMessaging {

    private AccessRequestMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries access request creation requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_add_access_request";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "healthUserCi",
            "healthWorkerCi",
            "clinicName",
            "specialtyNames"
    };

    /**
     * Field separator for the plain-text JMS payload.
     */
    public static final String FIELD_SEPARATOR = "|";
    
    /**
     * Separator for specialty names within the specialtyNames field.
     */
    public static final String SPECIALTY_SEPARATOR = ",";
}

