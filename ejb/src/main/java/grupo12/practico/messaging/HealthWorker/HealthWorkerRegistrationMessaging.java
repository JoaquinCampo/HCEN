package grupo12.practico.messaging.HealthWorker;

public final class HealthWorkerRegistrationMessaging {

    private HealthWorkerRegistrationMessaging() {
    }

    public static final String QUEUE_JNDI_NAME = "java:/jms/queue/queue_alta_health_worker";

    public static final String[] PAYLOAD_FIELDS = {
            "document",
            "documentType",
            "firstName",
            "lastName",
            "gender",
            "email",
            "phone",
            "address",
            "dateOfBirth",
            "password",
            "licenseNumber",
            "clinicIds"
    };

    public static final String FIELD_SEPARATOR = "|";
}
