package grupo12.practico.messaging.AccessPolicy;

/**
 * Centralizes JMS configuration details for the health worker access policy flow.
 */
public final class HealthWorkerAccessPolicyMessaging {

    private HealthWorkerAccessPolicyMessaging() {
        // Utility class
    }

    /**
     * JNDI name of the queue that carries health worker access policy creation requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_add_health_worker_access_policy";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "healthUserId",
            "healthWorkerCi",
            "clinicName"
    };

    /**
     * Field separator for the plain-text JMS payload.
     */
    public static final String FIELD_SEPARATOR = "|";
}

