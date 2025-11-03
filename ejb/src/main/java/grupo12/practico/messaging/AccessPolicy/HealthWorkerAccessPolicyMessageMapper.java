package grupo12.practico.messaging.AccessPolicy;

import java.util.Objects;

import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import jakarta.validation.ValidationException;

/**
 * Converts between {@link AddHealthWorkerAccessPolicyDTO} instances and the pipe-separated
 * payload used on the JMS queue.
 */
public final class HealthWorkerAccessPolicyMessageMapper {

    private static final int FIELD_COUNT = HealthWorkerAccessPolicyMessaging.PAYLOAD_FIELDS.length;

    private HealthWorkerAccessPolicyMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddHealthWorkerAccessPolicyDTO dto) {
        Objects.requireNonNull(dto, "health worker access policy dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getHealthUserId(), "healthUserId"),
                requireNoPipe(dto.getHealthWorkerCi(), "healthWorkerCi"),
                requireNoPipe(dto.getClinicName(), "clinicName")
        };

        return String.join(HealthWorkerAccessPolicyMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddHealthWorkerAccessPolicyDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddHealthWorkerAccessPolicyDTO dto = new AddHealthWorkerAccessPolicyDTO();
        dto.setHealthUserId(requireNotBlank(tokens[0], "healthUserId"));
        dto.setHealthWorkerCi(requireNotBlank(tokens[1], "healthWorkerCi"));
        dto.setClinicName(requireNotBlank(tokens[2], "clinicName"));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(HealthWorkerAccessPolicyMessaging.FIELD_SEPARATOR)) {
            throw new ValidationException("Field " + fieldName + " must not contain '|'");
        }
        return trimmed;
    }

    private static String requireNotBlank(String value, String fieldName) {
        if (value == null) {
            throw new ValidationException("Field " + fieldName + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Field " + fieldName + " is required");
        }
        return trimmed;
    }
}

