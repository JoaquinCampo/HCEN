package grupo12.practico.messaging.AccessPolicy;

import java.util.Objects;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import jakarta.validation.ValidationException;

/**
 * Converts between {@link AddClinicAccessPolicyDTO} instances and the
 * pipe-separated
 * payload used on the JMS queue.
 */
public final class ClinicAccessPolicyMessageMapper {

    private static final int FIELD_COUNT = ClinicAccessPolicyMessaging.PAYLOAD_FIELDS.length;

    private ClinicAccessPolicyMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddClinicAccessPolicyDTO dto) {
        Objects.requireNonNull(dto, "clinic access policy dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getHealthUserCi(), "healthUserCi"),
                requireNoPipe(dto.getClinicName(), "clinicName"),
                requireNoPipe(dto.getAccessRequestId(), "accessRequestId")
        };

        return String.join(ClinicAccessPolicyMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddClinicAccessPolicyDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddClinicAccessPolicyDTO dto = new AddClinicAccessPolicyDTO();
        dto.setHealthUserCi(requireNotBlank(tokens[0], "healthUserCi"));
        dto.setClinicName(requireNotBlank(tokens[1], "clinicName"));
        dto.setAccessRequestId(requireNotBlank(tokens[2], "accessRequestId"));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(ClinicAccessPolicyMessaging.FIELD_SEPARATOR)) {
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
