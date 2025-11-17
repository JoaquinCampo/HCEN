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

        String url = String.format("%sclinical-history/%s",
                config.getDocumentsApiBaseUrl(), healthUserCi);

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
                dto.setId(jsonObject.getString("doc_id", null));
                dto.setTitle(jsonObject.getString("title", null));
                dto.setDescription(jsonObject.getString("description", null));
                dto.setContent(jsonObject.getString("content", null));
                dto.setContentType(jsonObject.getString("content_type", null));

                String healthWorkerCi = jsonObject.getString("health_worker_ci", null);
                String clinicName = jsonObject.getString("clinic_name", null);

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

                dto.setContentUrl(jsonObject.getString("content_url", null));

                if (jsonObject.containsKey("created_at") && !jsonObject.isNull("created_at")) {
                    String createdAtStr = jsonObject.getString("created_at");
                    dto.setCreatedAt(ZonedDateTime.parse(createdAtStr).toLocalDateTime());
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
