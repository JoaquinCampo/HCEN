package grupo12.practico.messaging.Clinic;

import java.util.Objects;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
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

        ClinicAdminDTO admin = dto.getClinicAdmin() != null ? dto.getClinicAdmin() : new ClinicAdminDTO();

        String[] fields = new String[] {
                requireNoPipe(dto.getName(), "name"),
                requireNoPipe(dto.getEmail(), "email"),
                requireNoPipe(dto.getPhone(), "phone"),
                requireNoPipe(dto.getAddress(), "address"),
                requireNoPipe(admin.getName(), "clinicAdmin.name"),
                requireNoPipe(admin.getEmail(), "clinicAdmin.email"),
                optionalNoPipe(admin.getPhone()),
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
        dto.setEmail(requireNotBlank(tokens[1], "email"));
        dto.setPhone(requireNotBlank(tokens[2], "phone"));
        dto.setAddress(requireNotBlank(tokens[3], "address"));

        ClinicAdminDTO admin = new ClinicAdminDTO();
        admin.setName(requireNotBlank(tokens[4], "clinicAdmin.name"));
        admin.setEmail(requireNotBlank(tokens[5], "clinicAdmin.email"));
        admin.setPhone(emptyToNull(tokens[6]));
        dto.setClinicAdmin(admin);
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
