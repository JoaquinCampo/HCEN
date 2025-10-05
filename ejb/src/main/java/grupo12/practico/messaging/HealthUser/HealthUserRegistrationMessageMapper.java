package grupo12.practico.messaging.HealthUser;

import java.time.LocalDate;
import java.util.Objects;

import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import jakarta.validation.ValidationException;

/**
 * Converts between {@link AddHealthUserDTO} instances and the pipe-separated
 * payload used on the JMS queue.
 */
public final class HealthUserRegistrationMessageMapper {

    private static final int FIELD_COUNT = HealthUserRegistrationMessaging.PAYLOAD_FIELDS.length;

    private HealthUserRegistrationMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddHealthUserDTO dto) {
        Objects.requireNonNull(dto, "health user dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getDocument(), "document"),
                enumToString(dto.getDocumentType(), "documentType"),
                requireNoPipe(dto.getFirstName(), "firstName"),
                requireNoPipe(dto.getLastName(), "lastName"),
                enumToString(dto.getGender(), "gender"),
                optionalNoPipe(dto.getEmail()),
                optionalNoPipe(dto.getPhone()),
                optionalNoPipe(dto.getAddress()),
                toDateString(dto.getDateOfBirth()),
                requireNoPipe(dto.getPassword(), "password")
        };

        return String.join(HealthUserRegistrationMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddHealthUserDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddHealthUserDTO dto = new AddHealthUserDTO();
        dto.setDocument(requireNotBlank(tokens[0], "document"));
        dto.setDocumentType(parseDocumentType(tokens[1]));
        dto.setFirstName(requireNotBlank(tokens[2], "firstName"));
        dto.setLastName(requireNotBlank(tokens[3], "lastName"));
        dto.setGender(parseGender(tokens[4]));
        dto.setEmail(emptyToNull(tokens[5]));
        dto.setPhone(emptyToNull(tokens[6]));
        dto.setAddress(emptyToNull(tokens[7]));
        dto.setDateOfBirth(parseDate(tokens[8]));
        dto.setPassword(requireNotBlank(tokens[9], "password"));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(HealthUserRegistrationMessaging.FIELD_SEPARATOR)) {
            throw new ValidationException("Field " + fieldName + " must not contain '|'");
        }
        return trimmed;
    }

    private static String optionalNoPipe(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.contains(HealthUserRegistrationMessaging.FIELD_SEPARATOR)) {
            throw new ValidationException("Optional field must not contain '|'");
        }
        return trimmed;
    }

    private static String enumToString(Enum<?> value, String fieldName) {
        if (value == null) {
            throw new ValidationException("Field " + fieldName + " is required");
        }
        return value.name();
    }

    private static String toDateString(LocalDate date) {
        if (date == null) {
            throw new ValidationException("dateOfBirth is required");
        }
        return date.toString();
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

    private static LocalDate parseDate(String token) {
        String value = requireNotBlank(token, "dateOfBirth");
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            throw new ValidationException("Invalid dateOfBirth format: " + value);
        }
    }

    private static DocumentType parseDocumentType(String token) {
        String value = requireNotBlank(token, "documentType");
        try {
            return DocumentType.valueOf(value);
        } catch (Exception ex) {
            throw new ValidationException("Invalid documentType: " + value);
        }
    }

    private static Gender parseGender(String token) {
        String value = requireNotBlank(token, "gender");
        try {
            return Gender.valueOf(value);
        } catch (Exception ex) {
            throw new ValidationException("Invalid gender: " + value);
        }
    }
}
