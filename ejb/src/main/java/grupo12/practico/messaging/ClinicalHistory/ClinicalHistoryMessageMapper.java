package grupo12.practico.messaging.ClinicalHistory;

import java.util.Objects;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.validation.ValidationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts between {@link ClinicalHistoryRequestDTO} instances and JSON
 * payload used on the JMS queue.
 */
public final class ClinicalHistoryMessageMapper {

    private ClinicalHistoryMessageMapper() {
        // Utility class
    }

    public static String toMessage(ClinicalHistoryRequestDTO dto) {
        Objects.requireNonNull(dto, "clinical history request dto must not be null");

        JsonArrayBuilder specialtyArrayBuilder = Json.createArrayBuilder();
        if (dto.getSpecialtyNames() != null) {
            for (String specialty : dto.getSpecialtyNames()) {
                specialtyArrayBuilder.add(specialty);
            }
        }

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("healthUserCi", dto.getHealthUserCi() != null ? dto.getHealthUserCi() : "")
                .add("healthWorkerCi", dto.getHealthWorkerCi() != null ? dto.getHealthWorkerCi() : "")
                .add("clinicName", dto.getClinicName() != null ? dto.getClinicName() : "")
                .add("specialtyNames", specialtyArrayBuilder)
                .build();

        return jsonObject.toString();
    }

    public static ClinicalHistoryRequestDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonObject = reader.readObject();

            ClinicalHistoryRequestDTO dto = new ClinicalHistoryRequestDTO();
            dto.setHealthUserCi(requireString(jsonObject, "healthUserCi"));
            dto.setHealthWorkerCi(getStringOrNull(jsonObject, "healthWorkerCi"));
            dto.setClinicName(getStringOrNull(jsonObject, "clinicName"));

            List<String> specialtyNames = new ArrayList<>();
            if (jsonObject.containsKey("specialtyNames") && !jsonObject.isNull("specialtyNames")) {
                jsonObject.getJsonArray("specialtyNames").forEach(value -> {
                    if (value instanceof jakarta.json.JsonString) {
                        specialtyNames.add(((jakarta.json.JsonString) value).getString());
                    }
                });
            }
            dto.setSpecialtyNames(specialtyNames);

            return dto;
        } catch (Exception ex) {
            throw new ValidationException("Invalid JSON payload: " + ex.getMessage(), ex);
        }
    }

    private static String requireString(JsonObject jsonObject, String key) {
        if (!jsonObject.containsKey(key) || jsonObject.isNull(key)) {
            throw new ValidationException("Field " + key + " is required");
        }
        String value = jsonObject.getString(key);
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("Field " + key + " is required");
        }
        return value.trim();
    }

    private static String getStringOrNull(JsonObject jsonObject, String key) {
        if (!jsonObject.containsKey(key) || jsonObject.isNull(key)) {
            return null;
        }
        String value = jsonObject.getString(key);
        return value != null && !value.trim().isEmpty() ? value.trim() : null;
    }
}

