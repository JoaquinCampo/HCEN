package grupo12.practico.messaging.AccessPolicy.Specialty;

import java.util.Objects;

import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
import jakarta.validation.ValidationException;

/**
 * Converts between {@link AddSpecialtyAccessPolicyDTO} instances and the
 * pipe-separated
 * payload used on the JMS queue.
 */
public final class SpecialtyAccessPolicyMessageMapper {

    private static final int FIELD_COUNT = SpecialtyAccessPolicyMessaging.PAYLOAD_FIELDS.length;

    private SpecialtyAccessPolicyMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddSpecialtyAccessPolicyDTO dto) {
        Objects.requireNonNull(dto, "specialty access policy dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getHealthUserCi(), "healthUserCi"),
                requireNoPipe(dto.getSpecialtyName(), "specialtyName"),
                dto.getAccessRequestId() != null ? requireNoPipe(dto.getAccessRequestId(), "accessRequestId") : ""
        };

        return String.join(SpecialtyAccessPolicyMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddSpecialtyAccessPolicyDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddSpecialtyAccessPolicyDTO dto = new AddSpecialtyAccessPolicyDTO();
        dto.setHealthUserCi(requireNotBlank(tokens[0], "healthUserCi"));
        dto.setSpecialtyName(requireNotBlank(tokens[1], "specialtyName"));
        String accessRequestId = tokens[2];
        dto.setAccessRequestId(accessRequestId != null && !accessRequestId.trim().isEmpty() ? accessRequestId : null);
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(SpecialtyAccessPolicyMessaging.FIELD_SEPARATOR)) {
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

