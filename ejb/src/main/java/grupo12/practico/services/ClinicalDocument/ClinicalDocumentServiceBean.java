package grupo12.practico.services.ClinicalDocument;

import grupo12.practico.dtos.ClinicalDocument.CreateClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlRequestDTO;
import grupo12.practico.dtos.ClinicalDocument.PresignedUrlResponseDTO;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.validation.ValidationException;
import jakarta.ejb.EJB;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class ClinicalDocumentServiceBean implements ClinicalDocumentServiceLocal {

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    private static final Logger LOGGER = Logger.getLogger(ClinicalDocumentServiceBean.class.getName());
    private static final String DOCUMENTS_API_BASE_URL = getEnvOrDefault("app.external.documentsApiUrl",
            "http://host.docker.internal:8000/api/documents");
    private static final String DOCUMENTS_API_KEY = getEnvOrDefault("app.external.documentsApiKey", "");

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private final HttpClient httpClient;

    public ClinicalDocumentServiceBean() {
        this.httpClient = HttpClient.newHttpClient();
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

        String url = DOCUMENTS_API_BASE_URL + "/upload-url";

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
                .header("x-api-key", DOCUMENTS_API_KEY)
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

        String url = DOCUMENTS_API_BASE_URL;

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
                .header("x-api-key", DOCUMENTS_API_KEY)
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
}
