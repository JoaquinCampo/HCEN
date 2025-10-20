package grupo12.practico.messaging.ClinicalDocument;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import jakarta.validation.ValidationException;

public final class ClinicalDocumentRegistrationMessageMapper {

    private static final int FIELD_COUNT = ClinicalDocumentRegistrationMessaging.PAYLOAD_FIELDS.length;

    private ClinicalDocumentRegistrationMessageMapper() {
    }

    public static String toMessage(AddClinicalDocumentDTO dto) {
        Objects.requireNonNull(dto, "clinical document dto must not be null");

        String[] fields = new String[] {
                requireNoPipe(dto.getTitle(), "title"),
                requireNoPipe(dto.getContentUrl(), "contentUrl"),
                requireNoPipe(dto.getHealthUserId(), "healthUserId"),
                toList(dto.getHealthWorkerIds())
        };

        return String.join(ClinicalDocumentRegistrationMessaging.FIELD_SEPARATOR, fields);
    }

    public static AddClinicalDocumentDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        String[] tokens = message.split("\\|", -1);
        if (tokens.length != FIELD_COUNT) {
            throw new ValidationException("Unexpected field count: " + tokens.length);
        }

        AddClinicalDocumentDTO dto = new AddClinicalDocumentDTO();
        dto.setTitle(requireNotBlank(tokens[0], "title"));
        dto.setContentUrl(requireNotBlank(tokens[1], "contentUrl"));
        dto.setHealthUserId(requireNotBlank(tokens[2], "healthUserId"));
        dto.setHealthWorkerIds(fromList(tokens[3]));
        return dto;
    }

    private static String requireNoPipe(String value, String fieldName) {
        String trimmed = requireNotBlank(value, fieldName);
        if (trimmed.contains(ClinicalDocumentRegistrationMessaging.FIELD_SEPARATOR)) {
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

    private static String toList(Set<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return ""; // empty means none
        }
        // Disallow '|' and ',' in each id
        return ids.stream()
                .map(id -> {
                    String v = requireNotBlank(id, "healthWorkerId");
                    if (v.contains(ClinicalDocumentRegistrationMessaging.FIELD_SEPARATOR)
                            || v.contains(ClinicalDocumentRegistrationMessaging.LIST_SEPARATOR)) {
                        throw new ValidationException("Health worker IDs must not contain '|' or ',' characters");
                    }
                    return v;
                })
                .collect(Collectors.joining(ClinicalDocumentRegistrationMessaging.LIST_SEPARATOR));
    }

    private static Set<String> fromList(String token) {
        String value = token == null ? "" : token.trim();
        if (value.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return Stream.of(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
