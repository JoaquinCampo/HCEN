package grupo12.practico.messaging.HealthUser;

/**
 * Centralizes JMS configuration details for the health user registration flow.
 */
public final class HealthUserRegistrationMessaging {

    private HealthUserRegistrationMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries health user registration requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_alta_health_user";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "ci",
            "firstName",
            "lastName",
            "gender",
            "email",
            "phone",
            "address",
            "dateOfBirth",
            "clinicNames"
    };

    /**
     * Field separator for the plain-text JMS payload.
     */
    public static final String FIELD_SEPARATOR = "|";
}
