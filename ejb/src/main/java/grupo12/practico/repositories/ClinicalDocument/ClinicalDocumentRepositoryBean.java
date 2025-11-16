package grupo12.practico.repositories.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.AddClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ChatResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ChunkSourceDTO;
import grupo12.practico.dtos.ClinicalHistory.MessageDTO;
import grupo12.practico.repositories.NodoDocumentosConfig;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Local(ClinicalDocumentRepositoryLocal.class)
@Remote(ClinicalDocumentRepositoryRemote.class)
public class ClinicalDocumentRepositoryBean implements ClinicalDocumentRepositoryLocal {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @EJB
    private NodoDocumentosConfig config;

    private static final Logger LOGGER = Logger.getLogger(ClinicalDocumentRepositoryBean.class.getName());

    private final HttpClient httpClient;

    public ClinicalDocumentRepositoryBean() {
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    @Override
    public PresignedUrlResponseDTO getPresignedUploadUrl(PresignedUrlRequestDTO request) {
        String url = config.getDocumentsApiBaseUrl() + "/documents/upload-url";

        JsonObject jsonBody = Json.createObjectBuilder()
                .add("file_name", request.getFileName())
                .add("content_type", request.getContentType())
                .add("clinic_name", request.getProviderName())
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
    public String createClinicalDocument(AddClinicalDocumentDTO dto) {
        String url = config.getDocumentsApiBaseUrl() + "/documents";

        JsonObject jsonBody = Json.createObjectBuilder()
                .add("title", dto.getTitle())
                .add("description", dto.getDescription())
                .add("content", dto.getContent())
                .add("content_type", dto.getContentType())
                .add("content_url", dto.getContentUrl())
                .add("health_worker_ci", dto.getHealthWorkerCi())
                .add("health_user_ci", dto.getHealthUserCi())
                .add("clinic_name", dto.getClinicName())
                .add("provider_name", dto.getProviderName())
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

            return parseClinicalDocumentIdResponse(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while creating clinical document", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Error calling documents service", ex);
            throw new IllegalStateException("Unable to create clinical document", ex);
        }
    }

    @Override
    public ChatResponseDTO chat(ChatRequestDTO request) {
        String url = config.getDocumentsApiBaseUrl() + "/chat";

        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder()
                .add("query", request.getQuery())
                .add("health_user_ci", request.getHealthUserCi());

        if (request.getConversationHistory() != null && !request.getConversationHistory().isEmpty()) {
            JsonArrayBuilder historyBuilder = Json.createArrayBuilder();
            for (MessageDTO message : request.getConversationHistory()) {
                historyBuilder.add(Json.createObjectBuilder()
                        .add("role", message.getRole())
                        .add("content", message.getContent()));
            }
            jsonBuilder.add("conversation_history", historyBuilder);
        }

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

    private String parseClinicalDocumentIdResponse(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonObject jsonObject = reader.readObject();

            return jsonObject.getString("doc_id", null);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to parse clinical document response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse clinical document response", ex);
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