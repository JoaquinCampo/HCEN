package grupo12.practico.messaging.ClinicalDocument;

import java.util.Objects;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
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
 * Converts between {@link AddClinicalDocumentDTO} instances and JSON
 * payload used on the JMS queue.
 */
public final class CreateDocumentMessageMapper {

    private CreateDocumentMessageMapper() {
        // Utility class
    }

    public static String toMessage(AddClinicalDocumentDTO dto) {
        Objects.requireNonNull(dto, "create clinical document dto must not be null");

        JsonArrayBuilder specialtyArrayBuilder = Json.createArrayBuilder();
        if (dto.getSpecialtyNames() != null) {
            for (String specialty : dto.getSpecialtyNames()) {
                specialtyArrayBuilder.add(specialty);
            }
        }

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("title", dto.getTitle() != null ? dto.getTitle() : "")
                .add("description", dto.getDescription() != null ? dto.getDescription() : "")
                .add("content", dto.getContent() != null ? dto.getContent() : "")
                .add("contentType", dto.getContentType() != null ? dto.getContentType() : "")
                .add("contentUrl", dto.getContentUrl() != null ? dto.getContentUrl() : "")
                .add("healthWorkerCi", dto.getHealthWorkerCi() != null ? dto.getHealthWorkerCi() : "")
                .add("healthUserCi", dto.getHealthUserCi() != null ? dto.getHealthUserCi() : "")
                .add("clinicName", dto.getClinicName() != null ? dto.getClinicName() : "")
                .add("providerName", dto.getProviderName() != null ? dto.getProviderName() : "")
                .add("specialtyNames", specialtyArrayBuilder)
                .build();

        return jsonObject.toString();
    }

    public static AddClinicalDocumentDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonObject = reader.readObject();

            AddClinicalDocumentDTO dto = new AddClinicalDocumentDTO();
            dto.setTitle(getStringOrNull(jsonObject, "title"));
            dto.setDescription(getStringOrNull(jsonObject, "description"));
            dto.setContent(getStringOrNull(jsonObject, "content"));
            dto.setContentType(getStringOrNull(jsonObject, "contentType"));
            dto.setContentUrl(getStringOrNull(jsonObject, "contentUrl"));
            dto.setHealthWorkerCi(requireString(jsonObject, "healthWorkerCi"));
            dto.setHealthUserCi(requireString(jsonObject, "healthUserCi"));
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

