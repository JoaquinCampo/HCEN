package grupo12.practico.messaging.HealthWorker;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import grupo12.practico.dtos.HealthWorker.AddHealthWorkerDTO;
import grupo12.practico.models.DocumentType;
import grupo12.practico.models.Gender;
import jakarta.validation.ValidationException;

public final class HealthWorkerRegistrationMessageMapper {

    private static final int FIELD_COUNT = HealthWorkerRegistrationMessaging.PAYLOAD_FIELDS.length;

    private HealthWorkerRegistrationMessageMapper() {
    }

    public static String toMessage(AddHealthWorkerDTO dto) {
        Objects.requireNonNull(dto, "health worker dto must not be null");

        String normalizedBloodType = normalizeBloodType(dto.getBloodType());

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
                requireNoPipe(dto.getPassword(), "password"),
                requireNoPipe(dto.getLicenseNumber(), "licenseNumber"),
                requireNoPipe(normalizedBloodType, "bloodType"),
                setToString(dto.getClinicIds())
        };

        return String.join(HealthWorkerRegistrationMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddHealthWorkerDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddHealthWorkerDTO dto = new AddHealthWorkerDTO();
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
        dto.setLicenseNumber(requireNotBlank(tokens[10], "licenseNumber"));
        dto.setBloodType(normalizeBloodType(tokens[11]));
        dto.setClinicIds(parseStringSet(tokens[12]));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(HealthWorkerRegistrationMessaging.FIELD_SEPARATOR)) {
            throw new ValidationException("Field " + fieldName + " must not contain '|'");
        }
        return trimmed;
    }

    private static String optionalNoPipe(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.contains(HealthWorkerRegistrationMessaging.FIELD_SEPARATOR)) {
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

    private static String normalizeBloodType(String value) {
        String trimmed = requireNotBlank(value, "bloodType").trim().toUpperCase();
        if (!trimmed.matches("^(A|B|AB|O)[+-]$")) {
            throw new ValidationException("Invalid bloodType: " + trimmed);
        }
        return trimmed;
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

    private static String setToString(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        return set.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }

    private static Set<String> parseStringSet(String token) {
        if (token == null || token.trim().isEmpty()) {
            return Collections.emptySet();
        }
        String[] items = token.split(",");
        Set<String> result = new HashSet<>();
        for (String item : items) {
            String trimmed = item.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }
}
