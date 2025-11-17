package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.NodoDocumentosConfig;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;

import jakarta.validation.ValidationException;

@Stateless
@Local(HealthUserRepositoryLocal.class)
@Remote(HealthUserRepositoryRemote.class)
public class HealthUserRepositoryBean implements HealthUserRepositoryRemote {

    private static final Logger logger = Logger.getLogger(HealthUserRepositoryBean.class.getName());

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @EJB
    private NodoDocumentosConfig config;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private ClinicServiceLocal clinicService;

    private final HttpClient httpClient;

    public HealthUserRepositoryBean() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public List<HealthUser> findAllHealthUsers(String clinicName, String name, String ci, Integer pageIndex,
            Integer pageSize) {
        if (clinicName == null && name == null && ci == null) {
            TypedQuery<HealthUser> query = em.createQuery(
                    "SELECT h FROM HealthUser h ORDER BY h.lastName ASC, h.firstName ASC, h.id ASC",
                    HealthUser.class);

            if (pageSize != null && pageSize > 0) {
                int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
                query.setFirstResult(safePageIndex * pageSize);
                query.setMaxResults(pageSize);
            }

            return query.getResultList();
        }

        StringBuilder jpql = new StringBuilder("SELECT DISTINCT h FROM HealthUser h");

        boolean filterByClinic = clinicName != null && !clinicName.isEmpty();
        boolean filterByName = name != null && !name.isEmpty();
        boolean filterByCi = ci != null && !ci.isEmpty();

        if (filterByClinic) {
            jpql.append(" JOIN h.clinicNames clinic");
        }

        boolean hasCondition = false;
        if (filterByCi || filterByClinic || filterByName) {
            jpql.append(" WHERE");
        }

        if (filterByCi) {
            jpql.append(" LOWER(h.ci) LIKE :ci");
            hasCondition = true;
        }

        if (filterByClinic) {
            if (hasCondition) {
                jpql.append(" AND");
            }
            jpql.append(" LOWER(clinic) LIKE :clinic");
            hasCondition = true;
        }

        if (filterByName) {
            if (hasCondition) {
                jpql.append(" AND");
            }
            jpql.append(
                    " LOWER(CONCAT(CONCAT(COALESCE(h.firstName, ''), ' '), COALESCE(h.lastName, ''))) LIKE :name");
        }

        jpql.append(" ORDER BY h.lastName ASC, h.firstName ASC, h.id ASC");

        TypedQuery<HealthUser> query = em.createQuery(jpql.toString(), HealthUser.class);

        if (filterByCi) {
            query.setParameter("ci", "%" + ci.toLowerCase() + "%");
        }

        if (filterByClinic) {
            query.setParameter("clinic", "%" + clinicName.toLowerCase() + "%");
        }

        if (filterByName) {
            query.setParameter("name", "%" + name.toLowerCase() + "%");
        }

        if (pageSize != null && pageSize > 0) {
            int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
            query.setFirstResult(safePageIndex * pageSize);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Override
    public long countHealthUsers(String clinicName, String name, String ci) {
        if (clinicName == null && name == null && ci == null) {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(h) FROM HealthUser h", Long.class);
            return query.getSingleResult();
        }

        StringBuilder jpql = new StringBuilder("SELECT COUNT(DISTINCT h) FROM HealthUser h");

        boolean filterByClinic = clinicName != null && !clinicName.isEmpty();
        boolean filterByName = name != null && !name.isEmpty();
        boolean filterByCi = ci != null && !ci.isEmpty();

        if (filterByClinic) {
            jpql.append(" JOIN h.clinicNames clinic");
        }

        boolean hasCondition = false;
        if (filterByCi || filterByClinic || filterByName) {
            jpql.append(" WHERE");
        }

        if (filterByCi) {
            jpql.append(" LOWER(h.ci) LIKE :ci");
            hasCondition = true;
        }

        if (filterByClinic) {
            if (hasCondition) {
                jpql.append(" AND");
            }
            jpql.append(" LOWER(clinic) LIKE :clinic");
            hasCondition = true;
        }

        if (filterByName) {
            if (hasCondition) {
                jpql.append(" AND");
            }
            jpql.append(
                    " LOWER(CONCAT(CONCAT(COALESCE(h.firstName, ''), ' '), COALESCE(h.lastName, ''))) LIKE :name");
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

        if (filterByCi) {
            query.setParameter("ci", "%" + ci.toLowerCase() + "%");
        }

        if (filterByClinic) {
            query.setParameter("clinic", "%" + clinicName.toLowerCase() + "%");
        }

        if (filterByName) {
            query.setParameter("name", "%" + name.toLowerCase() + "%");
        }

        return query.getSingleResult();
    }

    @Override
    public HealthUser findHealthUserByCi(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.trim().isEmpty()) {
            throw new ValidationException("Health user CI must not be null or empty");
        }
        return em.createQuery("SELECT h FROM HealthUser h WHERE h.ci = :ci", HealthUser.class)
                .setParameter("ci", healthUserCi)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public HealthUser findHealthUserById(String healthUserId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()) {
            throw new ValidationException("Health user ID must not be null or empty");
        }
        return em.find(HealthUser.class, healthUserId);
    }

    @Override
    public HealthUser linkClinicToHealthUser(String healthUserCi, String clinicName) {
        HealthUser healthUser = findHealthUserByCi(healthUserCi);

        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        if (healthUser.getClinicNames().contains(clinicName)) {
            throw new ValidationException("Clinic already linked to health user");
        }

        healthUser.getClinicNames().add(clinicName);
        return em.merge(healthUser);
    }

    @Override
    public HealthUser createHealthUser(HealthUser healthUser) {
        if (healthUser == null) {
            throw new ValidationException("HealthUser must not be null");
        }
        logger.info("Persisting HealthUser with CI=" + healthUser.getCi());
        em.persist(healthUser);
        return healthUser;
    }

    @Override
    public List<ClinicalDocumentDTO> findHealthUserClinicalHistory(String healthUserCi) {

        // Ensure base URL ends with / and construct full path
        String baseUrl = config.getDocumentsApiBaseUrl();
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        String url = String.format("%sclinical-history/%s", baseUrl, healthUserCi);

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
                logger.log(Level.WARNING,
                        "Failed to fetch clinical history: HTTP {0}. Response body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to fetch clinical history: HTTP " + status);
            }

            return mapClinicalHistoryResponseToDto(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching clinical history", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error calling documents service for clinical history", ex);
            throw new IllegalStateException("Unable to fetch clinical history", ex);
        }
    }

    private List<ClinicalDocumentDTO> mapClinicalHistoryResponseToDto(String jsonBody) {
        try (JsonReader reader = Json.createReader(new StringReader(jsonBody))) {
            JsonArray jsonArray = reader.readArray();
            List<ClinicalDocumentDTO> documents = new ArrayList<>();

            for (JsonValue value : jsonArray) {
                JsonObject jsonObject = value.asJsonObject();
                ClinicalDocumentDTO dto = new ClinicalDocumentDTO();
                
                // doc_id is UUID, convert to String
                if (jsonObject.containsKey("doc_id") && !jsonObject.isNull("doc_id")) {
                    String docId = jsonObject.getString("doc_id");
                    dto.setId(docId);
                }

                // health_worker_ci or created_by (both are valid in the schema)
                String healthWorkerCi = null;
                if (jsonObject.containsKey("health_worker_ci") && !jsonObject.isNull("health_worker_ci")) {
                    healthWorkerCi = jsonObject.getString("health_worker_ci");
                } else if (jsonObject.containsKey("created_by") && !jsonObject.isNull("created_by")) {
                    healthWorkerCi = jsonObject.getString("created_by");
                }
                
                String clinicName = null;
                if (jsonObject.containsKey("clinic_name") && !jsonObject.isNull("clinic_name")) {
                    clinicName = jsonObject.getString("clinic_name");
                }

                if (healthWorkerCi != null && clinicName != null) {
                    try {
                        dto.setHealthWorker(healthWorkerService.findByClinicAndCi(clinicName, healthWorkerCi));
                    } catch (Exception ex) {
                        logger.log(Level.WARNING,
                                "Failed to fetch HealthWorker for CI: " + healthWorkerCi + " and clinic: " + clinicName,
                                ex);
                    }
                }

                if (clinicName != null) {
                    try {
                        dto.setClinic(clinicService.findClinicByName(clinicName));
                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "Failed to fetch Clinic for name: " + clinicName, ex);
                    }
                }

                // content_url or s3_url (content_url is the serialized name, but s3_url is also accepted)
                String contentUrl = null;
                if (jsonObject.containsKey("content_url") && !jsonObject.isNull("content_url")) {
                    contentUrl = jsonObject.getString("content_url");
                } else if (jsonObject.containsKey("s3_url") && !jsonObject.isNull("s3_url")) {
                    contentUrl = jsonObject.getString("s3_url");
                }
                dto.setContentUrl(contentUrl);

                // Map new fields from DocumentResponse schema
                if (jsonObject.containsKey("title") && !jsonObject.isNull("title")) {
                    dto.setTitle(jsonObject.getString("title"));
                }
                
                if (jsonObject.containsKey("description") && !jsonObject.isNull("description")) {
                    dto.setDescription(jsonObject.getString("description"));
                }
                
                if (jsonObject.containsKey("content") && !jsonObject.isNull("content")) {
                    dto.setContent(jsonObject.getString("content"));
                }
                
                if (jsonObject.containsKey("content_type") && !jsonObject.isNull("content_type")) {
                    dto.setContentType(jsonObject.getString("content_type"));
                }

                // Parse created_at timestamp (ISO 8601 format)
                if (jsonObject.containsKey("created_at") && !jsonObject.isNull("created_at")) {
                    try {
                        String createdAtStr = jsonObject.getString("created_at");
                        // Handle both ZonedDateTime and LocalDateTime formats
                        if (createdAtStr.contains("T") && createdAtStr.contains("Z")) {
                            // ISO 8601 with timezone (e.g., "2024-01-01T12:00:00Z")
                            dto.setCreatedAt(ZonedDateTime.parse(createdAtStr).toLocalDateTime());
                        } else if (createdAtStr.contains("T") && createdAtStr.contains("+")) {
                            // ISO 8601 with timezone offset (e.g., "2024-01-01T12:00:00+00:00")
                            dto.setCreatedAt(ZonedDateTime.parse(createdAtStr).toLocalDateTime());
                        } else if (createdAtStr.contains("T")) {
                            // ISO 8601 without timezone (e.g., "2024-01-01T12:00:00")
                            dto.setCreatedAt(LocalDateTime.parse(createdAtStr));
                        } else {
                            // Fallback: try ZonedDateTime parse
                            dto.setCreatedAt(ZonedDateTime.parse(createdAtStr).toLocalDateTime());
                        }
                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "Failed to parse created_at: " + jsonObject.getString("created_at"), ex);
                    }
                }

                documents.add(dto);
            }

            return documents;
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Failed to parse clinical history response: " + jsonBody, ex);
            throw new IllegalStateException("Failed to parse clinical history response", ex);
        }
    }
}
