package grupo12.practico.messaging.Clinic;

import java.util.Objects;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import jakarta.validation.ValidationException;

/**
 * Converts between {@link AddClinicDTO} instances and the pipe-separated
 * payload used on the JMS queue.
 */
public final class ClinicRegistrationMessageMapper {

    private static final int FIELD_COUNT = ClinicRegistrationMessaging.PAYLOAD_FIELDS.length;

    private ClinicRegistrationMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddClinicDTO dto) {
        Objects.requireNonNull(dto, "clinic dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getName(), "name"),
                optionalNoPipe(dto.getEmail()),
                optionalNoPipe(dto.getPhone()),
                optionalNoPipe(dto.getAddress()),
                optionalNoPipe(dto.getDomain()),
                optionalNoPipe(dto.getType())
        };

        return String.join(ClinicRegistrationMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddClinicDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddClinicDTO dto = new AddClinicDTO();
        dto.setName(requireNotBlank(tokens[0], "name"));
        dto.setEmail(emptyToNull(tokens[1]));
        dto.setPhone(emptyToNull(tokens[2]));
        dto.setAddress(emptyToNull(tokens[3]));
        dto.setDomain(emptyToNull(tokens[4]));
        dto.setType(emptyToNull(tokens[5]));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(ClinicRegistrationMessaging.FIELD_SEPARATOR)) {
            throw new ValidationException("Field " + fieldName + " must not contain '|'");
        }
        return trimmed;
    }

    private static String optionalNoPipe(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.contains(ClinicRegistrationMessaging.FIELD_SEPARATOR)) {
            throw new ValidationException("Optional field must not contain '|'");
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

    private static String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
