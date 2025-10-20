package grupo12.practico.messaging.ClinicalDocument;

/**
 * Centralizes JMS configuration details for the clinical document registration
 * flow.
 */
public final class ClinicalDocumentRegistrationMessaging {

    private ClinicalDocumentRegistrationMessaging() {
    }

    /**
     * JNDI name of the queue that carries clinical document registration requests.
     */
    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_alta_clinical_document";

    /**
     * Ordered list of payload fields used in the pipe-separated message.
     */
    public static final String[] PAYLOAD_FIELDS = {
            "title",
            "contentUrl",
            "healthUserId",
            "healthWorkerIds" // comma-separated list of IDs
    };

    /** Field separator for the plain-text JMS payload. */
    public static final String FIELD_SEPARATOR = "|";

    /**
     * List separator used to serialize collections inside a single payload field.
     */
    public static final String LIST_SEPARATOR = ",";
}
