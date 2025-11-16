package grupo12.practico.messaging.ClinicalDocument.Chat;

import java.util.Objects;

import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.MessageDTO;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.validation.ValidationException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts between {@link ChatRequestDTO} instances and JSON
 * payload used on the JMS queue.
 */
public final class ChatMessageMapper {

    private ChatMessageMapper() {
        // Utility class
    }

    public static String toMessage(ChatRequestDTO dto) {
        Objects.requireNonNull(dto, "chat request dto must not be null");

        JsonArrayBuilder conversationArrayBuilder = Json.createArrayBuilder();
        if (dto.getConversationHistory() != null) {
            for (MessageDTO message : dto.getConversationHistory()) {
                JsonObject messageObject = Json.createObjectBuilder()
                        .add("role", message.getRole() != null ? message.getRole() : "")
                        .add("content", message.getContent() != null ? message.getContent() : "")
                        .build();
                conversationArrayBuilder.add(messageObject);
            }
        }

        JsonObject jsonObject = Json.createObjectBuilder()
                .add("query", dto.getQuery() != null ? dto.getQuery() : "")
                .add("conversationHistory", conversationArrayBuilder)
                .add("healthUserCi", dto.getHealthUserCi() != null ? dto.getHealthUserCi() : "")
                .add("documentId", dto.getDocumentId() != null ? dto.getDocumentId() : "")
                .build();

        return jsonObject.toString();
    }

    public static ChatRequestDTO fromMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new ValidationException("Message payload must not be empty");
        }

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonObject = reader.readObject();

            ChatRequestDTO dto = new ChatRequestDTO();
            dto.setQuery(getStringOrNull(jsonObject, "query"));
            dto.setHealthUserCi(requireString(jsonObject, "healthUserCi"));
            dto.setDocumentId(getStringOrNull(jsonObject, "documentId"));

            List<MessageDTO> conversationHistory = new ArrayList<>();
            if (jsonObject.containsKey("conversationHistory") && !jsonObject.isNull("conversationHistory")) {
                jsonObject.getJsonArray("conversationHistory").forEach(value -> {
                    if (value instanceof JsonObject) {
                        JsonObject messageObject = (JsonObject) value;
                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setRole(getStringOrNull(messageObject, "role"));
                        messageDTO.setContent(getStringOrNull(messageObject, "content"));
                        conversationHistory.add(messageDTO);
                    }
                });
            }
            dto.setConversationHistory(conversationHistory);

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

