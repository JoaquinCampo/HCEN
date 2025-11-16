package grupo12.practico.messaging.ClinicalDocument.PresignedUrl;

import java.util.Objects;

import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.validation.ValidationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts between {@link PresignedUrlRequestDTO} instances and JSON
 * payload used on the JMS queue.
 */
public final class PresignedUrlMessageMapper {

    private PresignedUrlMessageMapper() {
        // Utility class
    }

    public static String toMessage(PresignedUrlRequestDTO dto) {
        Objects.requireNonNull(dto, "presigned URL request dto must not be null");

        JsonArrayBuilder specialtyArrayBuilder = Json.createArrayBuilder();
        if (dto.getSpecialtyNames() != null) {
            for (String specialty : dto.getSpecialtyNames()) {
                specialtyArrayBuilder.add(specialty);
            }
        }

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("fileName", dto.getFileName() != null ? dto.getFileName() : "")
                .add("contentType", dto.getContentType() != null ? dto.getContentType() : "")
                .add("healthUserCi", dto.getHealthUserCi() != null ? dto.getHealthUserCi() : "")
                .add("healthWorkerCi", dto.getHealthWorkerCi() != null ? dto.getHealthWorkerCi() : "")
                .add("clinicName", dto.getClinicName() != null ? dto.getClinicName() : "")
                .add("providerName", dto.getProviderName() != null ? dto.getProviderName() : "")
                .add("specialtyNames", specialtyArrayBuilder)
                .build();

        return jsonObject.toString();
    }

    public static PresignedUrlRequestDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonObject = reader.readObject();

            PresignedUrlRequestDTO dto = new PresignedUrlRequestDTO();
            dto.setFileName(getStringOrNull(jsonObject, "fileName"));
            dto.setContentType(getStringOrNull(jsonObject, "contentType"));
            dto.setHealthUserCi(requireString(jsonObject, "healthUserCi"));
            dto.setHealthWorkerCi(getStringOrNull(jsonObject, "healthWorkerCi"));
            dto.setClinicName(getStringOrNull(jsonObject, "clinicName"));
            dto.setProviderName(getStringOrNull(jsonObject, "providerName"));

            List<String> specialtyNames = new ArrayList<>();
            if (jsonObject.containsKey("specialtyNames") && !jsonObject.isNull("specialtyNames")) {
                jsonObject.getJsonArray("specialtyNames").forEach(value -> {
                    if (value instanceof JsonString) {
                        specialtyNames.add(((JsonString) value).getString());
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

