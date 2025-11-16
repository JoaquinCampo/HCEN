package grupo12.practico.messaging.AccessRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        String specialtyNamesStr = "";
        if (dto.getSpecialtyNames() != null && !dto.getSpecialtyNames().isEmpty()) {
            specialtyNamesStr = dto.getSpecialtyNames().stream()
                    .map(name -> {
                        if (name.contains(AccessRequestMessaging.FIELD_SEPARATOR)) {
                            throw new ValidationException("Specialty name must not contain '|'");
                        }
                        if (name.contains(AccessRequestMessaging.SPECIALTY_SEPARATOR)) {
                            throw new ValidationException("Specialty name must not contain ','");
                        }
                        return name.trim();
                    })
                    .collect(Collectors.joining(AccessRequestMessaging.SPECIALTY_SEPARATOR));
        }

        String[] fields = new String[] {
                requireNoPipe(dto.getHealthUserCi(), "healthUserCi"),
                requireNoPipe(dto.getHealthWorkerCi(), "healthWorkerCi"),
                requireNoPipe(dto.getClinicName(), "clinicName"),
                specialtyNamesStr
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
        
        // Parse specialty names
        String specialtyNamesStr = tokens[3];
        if (specialtyNamesStr != null && !specialtyNamesStr.trim().isEmpty()) {
            List<String> specialtyNames = Arrays.stream(specialtyNamesStr.split(AccessRequestMessaging.SPECIALTY_SEPARATOR))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            dto.setSpecialtyNames(specialtyNames);
        } else {
            dto.setSpecialtyNames(Collections.emptyList());
        }
        
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

