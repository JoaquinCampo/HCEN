package grupo12.practico.messaging.AccessPolicy.Specialty;

/**
 * Centralizes JMS configuration details for the specialty access policy
 * flow.
 */
public final class SpecialtyAccessPolicyMessaging {

    private SpecialtyAccessPolicyMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries specialty access policy creation
     * requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_add_specialty_access_policy";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "healthUserCi",
            "specialtyName",
            "accessRequestId"
    };

    /**
     * Field separator for the plain-text JMS payload.
     */
    public static final String FIELD_SEPARATOR = "|";
}

