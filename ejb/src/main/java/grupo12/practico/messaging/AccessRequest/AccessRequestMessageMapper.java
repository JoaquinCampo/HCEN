package grupo12.practico.messaging.AccessRequest;

import java.util.Objects;

import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import jakarta.validation.ValidationException;

/**
 * Converts between {@link AddAccessRequestDTO} instances and the pipe-separated
 * payload used on the JMS queue.
 */
public final class AccessRequestMessageMapper {

    private static final int FIELD_COUNT = AccessRequestMessaging.PAYLOAD_FIELDS.length;

    private AccessRequestMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddAccessRequestDTO dto) {
        Objects.requireNonNull(dto, "access request dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getHealthUserCi(), "healthUserCi"),
                requireNoPipe(dto.getHealthWorkerCi(), "healthWorkerCi"),
                requireNoPipe(dto.getClinicName(), "clinicName")
        };

        return String.join(AccessRequestMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddAccessRequestDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddAccessRequestDTO dto = new AddAccessRequestDTO();
        dto.setHealthUserCi(requireNotBlank(tokens[0], "healthUserCi"));
        dto.setHealthWorkerCi(requireNotBlank(tokens[1], "healthWorkerCi"));
        dto.setClinicName(requireNotBlank(tokens[2], "clinicName"));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(AccessRequestMessaging.FIELD_SEPARATOR)) {
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

