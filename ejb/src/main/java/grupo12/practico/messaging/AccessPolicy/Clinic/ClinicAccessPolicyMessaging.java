package grupo12.practico.messaging.AccessPolicy.Clinic;

/**
 * Centralizes JMS configuration details for the clinic access policy flow.
 */
public final class ClinicAccessPolicyMessaging {

    private ClinicAccessPolicyMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries clinic access policy creation requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_add_clinic_access_policy";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "healthUserCi",
            "clinicName",
            "accessRequestId"
    };

    /**
     * Field separator for the plain-text JMS payload.
     */
    public static final String FIELD_SEPARATOR = "|";
}
