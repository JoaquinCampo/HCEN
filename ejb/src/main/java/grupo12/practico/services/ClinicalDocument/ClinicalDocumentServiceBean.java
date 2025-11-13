package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.ChunkSourceDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.MessageDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.repositories.NodoDocumentosConfig;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.validation.ValidationException;
import jakarta.ejb.EJB;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ClinicalDocumentServiceBean implements ClinicalDocumentServiceLocal {

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @EJB
    private NodoDocumentosConfig config;

    private static final Logger LOGGER = Logger.getLogger(ClinicalDocumentServiceBean.class.getName());

    private final HttpClient httpClient;

    public ClinicalDocumentServiceBean() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    @Override
    public PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request) {
        validatePresignedUrlRequest(request);

        boolean hasClinicAccess = accessPolicyService.hasClinicAccess(
                request.getHealthUserCi(),
                request.getClinicName());

        boolean hasHealthWorkerAccess = accessPolicyService.hasHealthWorkerAccess(
                request.getHealthUserCi(),
                request.getHealthWorkerCi());

        if (!hasClinicAccess && !hasHealthWorkerAccess) {
            throw new ValidationException(
                    "Health worker does not have access to upload documents for the specified clinic or health worker.");
        }

        String url = config.getDocumentsApiBaseUrl() + "/documents/upload-url";

        // Build JSON request body
        JsonObject jsonBody = Json.createObjectBuilder()
                .add("file_name", request.getFileName())
                .add("content_type", request.getContentType())
                .add("clinic_name", request.getProviderName()) // TODO change field name
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("x-api-key", config.getDocumentsApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 200 && status != 201) {
                LOGGER.log(Level.WARNING,
                        "Failed to get presigned URL: HTTP {0}. Response body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to get presigned URL: HTTP " + status);
            }

            return parsePresignedUrlResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while requesting presigned URL", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error calling documents service for presigned URL", ex);
            throw new IllegalStateException("Unable to request presigned URL", ex);
        }
    }

    @Override
    public DocumentResponseDTO createClinicalDocument(CreateClinicalDocumentDTO dto) {
        validateCreateClinicalDocumentRequest(dto);

        String url = config.getDocumentsApiBaseUrl() + "/documents";

        // Build JSON request body
        JsonObject jsonBody = Json.createObjectBuilder()
                .add("created_by", dto.getHealthWorkerCi())
                .add("health_user_ci", dto.getHealthUserCi())
                .add("clinic_name", dto.getClinicName())
                .add("s3_url", dto.getS3Url())
                .build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("x-api-key", config.getDocumentsApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 200 && status != 201) {
                LOGGER.log(Level.WARNING,
                        "Failed to create clinical document: HTTP {0}. Response body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to create clinical document: HTTP " + status);
            }

            return parseClinicalDocumentResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while creating clinical document", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error calling documents service", ex);
            throw new IllegalStateException("Unable to create clinical document", ex);
        }
    }

    private void validatePresignedUrlRequest(PresignedUrlRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Presigned URL request must not be null");
        }
        if (request.getFileName() == null || request.getFileName().trim().isEmpty()) {
            throw new ValidationException("File name is required");
        }
        if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {
            throw new ValidationException("Content type is required");
        }
        if (request.getClinicName() == null || request.getClinicName().trim().isEmpty()) {
            throw new ValidationException("Clinic name is required");
        }
    }

    private void validateCreateClinicalDocumentRequest(CreateClinicalDocumentDTO dto) {
        if (dto == null) {
            throw new ValidationException("Clinical document creation request must not be null");
        }
        if (dto.getHealthWorkerCi() == null || dto.getHealthWorkerCi().trim().isEmpty()) {
            throw new ValidationException("Created by (health worker CI) is required");
        }
        if (dto.getHealthUserCi() == null || dto.getHealthUserCi().trim().isEmpty()) {
            throw new ValidationException("Health user CI is required");
        }
        if (dto.getClinicName() == null || dto.getClinicName().trim().isEmpty()) {
            throw new ValidationException("Clinic name is required");
        }
        if (dto.getS3Url() == null || dto.getS3Url().trim().isEmpty()) {
            throw new ValidationException("S3 URL is required");
        }
    }

    private PresignedUrlResponseDTO parsePresignedUrlResponse(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonObject jsonObject = reader.readObject();

            PresignedUrlResponseDTO dto = new PresignedUrlResponseDTO();
            dto.setUploadUrl(jsonObject.getString("upload_url", null));
            dto.setS3Url(jsonObject.getString("s3_url", null));
            dto.setObjectKey(jsonObject.getString("object_key", null));
            dto.setExpiresInSeconds(jsonObject.getInt("expires_in_seconds", 3600));

            return dto;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to parse presigned URL response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse presigned URL response", ex);
        }
    }

    private DocumentResponseDTO parseClinicalDocumentResponse(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonObject jsonObject = reader.readObject();

            DocumentResponseDTO dto = new DocumentResponseDTO();
            dto.setDocId(jsonObject.getString("doc_id", null));
            dto.setCreatedBy(jsonObject.getString("created_by", null));
            dto.setHealthUserCi(jsonObject.getString("health_user_ci", null));
            dto.setClinicName(jsonObject.getString("clinic_name", null));
            dto.setS3Url(jsonObject.getString("s3_url", null));

            // Parse the created_at timestamp if present
            if (jsonObject.containsKey("created_at") && !jsonObject.isNull("created_at")) {
                String createdAtStr = jsonObject.getString("created_at");
                // Parse ISO 8601 timestamp with 'Z' suffix and convert to LocalDateTime
                dto.setCreatedAt(ZonedDateTime.parse(createdAtStr).toLocalDateTime());
            }

            return dto;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to parse clinical document response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse clinical document response", ex);
        }
    }

    @Override
    public List<DocumentResponseDTO> fetchClinicalHistory(String healthUserCi, String healthWorkerCi,
            String clinicName) {
        if (healthUserCi == null || healthUserCi.trim().isEmpty()) {
            throw new ValidationException("Health user CI is required");
        }
        if (healthWorkerCi == null || healthWorkerCi.trim().isEmpty()) {
            throw new ValidationException("Health worker CI is required");
        }
        if (clinicName == null || clinicName.trim().isEmpty()) {
            throw new ValidationException("Clinic name is required");
        }

        String url = String.format("%s/clinical-history/%s?health_worker_ci=%s&clinic_name=%s",
                config.getDocumentsApiBaseUrl(), healthUserCi, healthWorkerCi, clinicName);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("x-api-key", config.getDocumentsApiKey())
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 200) {
                LOGGER.log(Level.WARNING,
                        "Failed to fetch clinical history: HTTP {0}. Response body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to fetch clinical history: HTTP " + status);
            }

            return parseClinicalHistoryResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching clinical history", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error calling documents service for clinical history", ex);
            throw new IllegalStateException("Unable to fetch clinical history", ex);
        }
    }

    @Override
    public List<ClinicalHistoryAccessLogResponseDTO> fetchHealthWorkerAccessHistory(String healthWorkerCi,
            String healthUserCi) {
        if (healthWorkerCi == null || healthWorkerCi.trim().isEmpty()) {
            throw new ValidationException("Health worker CI is required");
        }

        String url = String.format("%s/clinical-history/health-workers/%s/access-history",
                config.getDocumentsApiBaseUrl(), healthWorkerCi);

        if (healthUserCi != null && !healthUserCi.trim().isEmpty()) {
            url += "?health_user_ci=" + healthUserCi;
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("x-api-key", config.getDocumentsApiKey())
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 200) {
                LOGGER.log(Level.WARNING,
                        "Failed to fetch access history: HTTP {0}. Response body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to fetch access history: HTTP " + status);
            }

            return parseAccessHistoryResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching access history", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error calling documents service for access history", ex);
            throw new IllegalStateException("Unable to fetch access history", ex);
        }
    }

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Chat request must not be null");
        }
        if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new ValidationException("Query is required");
        }
        if (request.getHealthUserCi() == null || request.getHealthUserCi().trim().isEmpty()) {
            throw new ValidationException("Health user CI is required");
        }

        String url = config.getDocumentsApiBaseUrl() + "/chat";

        // Build JSON request body
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                .add("query", request.getQuery())
                .add("health_user_ci", request.getHealthUserCi());

        // Add conversation history if present
        if (request.getConversationHistory() != null && !request.getConversationHistory().isEmpty()) {
            JsonArrayBuilder historyBuilder = Json.createArrayBuilder();
            for (MessageDTO message : request.getConversationHistory()) {
                historyBuilder.add(Json.createObjectBuilder()
                        .add("role", message.getRole())
                        .add("content", message.getContent()));
            }
            jsonBuilder.add("conversation_history", historyBuilder);
        }

        // Add document_id if present
        if (request.getDocumentId() != null && !request.getDocumentId().trim().isEmpty()) {
            jsonBuilder.add("document_id", request.getDocumentId());
        }

        JsonObject jsonBody = jsonBuilder.build();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("x-api-key", config.getDocumentsApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 200) {
                LOGGER.log(Level.WARNING,
                        "Failed to process chat query: HTTP {0}. Response body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to process chat query: HTTP " + status);
            }

            return parseChatResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while processing chat query", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error calling documents service for chat", ex);
            throw new IllegalStateException("Unable to process chat query", ex);
        }
    }

    private List<DocumentResponseDTO> parseClinicalHistoryResponse(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonArray jsonArray = reader.readArray();
            List<DocumentResponseDTO> documents = new ArrayList<>();

            for (JsonValue value : jsonArray) {
                JsonObject jsonObject = value.asJsonObject();
                DocumentResponseDTO dto = new DocumentResponseDTO();
                dto.setDocId(jsonObject.getString("doc_id", null));
                dto.setCreatedBy(jsonObject.getString("created_by", null));
                dto.setHealthUserCi(jsonObject.getString("health_user_ci", null));
                dto.setClinicName(jsonObject.getString("clinic_name", null));
                dto.setS3Url(jsonObject.getString("s3_url", null));

                if (jsonObject.containsKey("created_at") && !jsonObject.isNull("created_at")) {
                    String createdAtStr = jsonObject.getString("created_at");
                    dto.setCreatedAt(ZonedDateTime.parse(createdAtStr).toLocalDateTime());
                }

                documents.add(dto);
            }

            return documents;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to parse clinical history response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse clinical history response", ex);
        }
    }

    private List<ClinicalHistoryAccessLogResponseDTO> parseAccessHistoryResponse(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonArray jsonArray = reader.readArray();
            List<ClinicalHistoryAccessLogResponseDTO> logs = new ArrayList<>();

            for (JsonValue value : jsonArray) {
                JsonObject jsonObject = value.asJsonObject();
                ClinicalHistoryAccessLogResponseDTO dto = new ClinicalHistoryAccessLogResponseDTO();
                dto.setId(Long.valueOf(jsonObject.getInt("id")));
                dto.setHealthUserCi(jsonObject.getString("health_user_ci", null));
                dto.setHealthWorkerCi(jsonObject.getString("health_worker_ci", null));
                dto.setClinicName(jsonObject.getString("clinic_name", null));
                dto.setViewed(jsonObject.getBoolean("viewed"));

                if (jsonObject.containsKey("requested_at") && !jsonObject.isNull("requested_at")) {
                    String requestedAtStr = jsonObject.getString("requested_at");
                    dto.setRequestedAt(ZonedDateTime.parse(requestedAtStr).toLocalDateTime());
                }

                if (jsonObject.containsKey("decision_reason") && !jsonObject.isNull("decision_reason")) {
                    dto.setDecisionReason(jsonObject.getString("decision_reason"));
                }

                logs.add(dto);
            }

            return logs;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to parse access history response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse access history response", ex);
        }
    }

    private ChatResponseDTO parseChatResponse(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonObject jsonObject = reader.readObject();

            ChatResponseDTO dto = new ChatResponseDTO();
            dto.setAnswer(jsonObject.getString("answer", null));

            // Parse sources array
            if (jsonObject.containsKey("sources")) {
                JsonArray sourcesArray = jsonObject.getJsonArray("sources");
                List<ChunkSourceDTO> sources = new ArrayList<>();

                for (JsonValue value : sourcesArray) {
                    JsonObject sourceObject = value.asJsonObject();
                    ChunkSourceDTO source = new ChunkSourceDTO();
                    source.setDocumentId(sourceObject.getString("document_id", null));
                    source.setChunkId(sourceObject.getString("chunk_id", null));
                    source.setText(sourceObject.getString("text", null));
                    source.setSimilarityScore(sourceObject.getJsonNumber("similarity_score").doubleValue());

                    if (sourceObject.containsKey("page_number") && !sourceObject.isNull("page_number")) {
                        source.setPageNumber(sourceObject.getInt("page_number"));
                    }

                    if (sourceObject.containsKey("section_title") && !sourceObject.isNull("section_title")) {
                        source.setSectionTitle(sourceObject.getString("section_title"));
                    }

                    sources.add(source);
                }

                dto.setSources(sources);
            }

            return dto;
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to parse chat response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse chat response", ex);
        }
    }
}
