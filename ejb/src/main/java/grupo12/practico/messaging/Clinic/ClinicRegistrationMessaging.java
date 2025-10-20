package grupo12.practico.messaging.Clinic;

/**
 * Centralizes JMS configuration details for the clinic registration flow.
 */
public final class ClinicRegistrationMessaging {

    private ClinicRegistrationMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries clinic registration requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_add_clinic";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "name",
            "email",
            "phone",
            "address",
            "adminName",
            "adminEmail",
            "adminPhone"
    };

    /**
     * Field separator for the plain-text JMS payload.
     */
    public static final String FIELD_SEPARATOR = "|";
}
