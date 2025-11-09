package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthUser.ClinicalDocumentDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.models.HealthUser;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import jakarta.validation.ValidationException;

@Stateless
@Local(HealthUserRepositoryLocal.class)
@Remote(HealthUserRepositoryRemote.class)
public class HealthUserRepositoryBean implements HealthUserRepositoryRemote {

    private static final Logger logger = Logger.getLogger(HealthUserRepositoryBean.class.getName());
    private static final String CLINICAL_HISTORY_BASE_URL = getEnvOrDefault("app.external.clinicalHistoryApiUrl",
            "http://host.docker.internal:3000/api/clinical-history");

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getProperty(key);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    private final HttpClient httpClient;

    public HealthUserRepositoryBean() {
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public List<HealthUser> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize) {
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

        String trimmedClinic = clinicName != null ? clinicName.trim() : null;
        String trimmedName = name != null ? name.trim() : null;
        String trimmedCi = ci != null ? ci.trim() : null;

        StringBuilder jpql = new StringBuilder("SELECT DISTINCT h FROM HealthUser h");

        boolean filterByClinic = trimmedClinic != null && !trimmedClinic.isEmpty();
        boolean filterByName = trimmedName != null && !trimmedName.isEmpty();
        boolean filterByCi = trimmedCi != null && !trimmedCi.isEmpty();

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
            query.setParameter("ci", "%" + trimmedCi.toLowerCase() + "%");
        }

        if (filterByClinic) {
            query.setParameter("clinic", "%" + trimmedClinic.toLowerCase() + "%");
        }

        if (filterByName) {
            query.setParameter("name", "%" + trimmedName.toLowerCase() + "%");
        }

        if (pageSize != null && pageSize > 0) {
            int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
            query.setFirstResult(safePageIndex * pageSize);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Override
    public HealthUser findByCi(String healthUserCi) {
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
    public HealthUser findById(String healthUserId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()) {
            throw new ValidationException("Health user ID must not be null or empty");
        }
        return em.find(HealthUser.class, healthUserId);
    }

    @Override
    public HealthUser linkClinicToHealthUser(String healthUserCi, String clinicName) {
        HealthUser healthUser = findByCi(healthUserCi);
        boolean alreadyLinked = healthUser.getClinicNames().stream()
                .anyMatch(existing -> existing != null && existing.equalsIgnoreCase(clinicName));
        if (!alreadyLinked) {
            healthUser.getClinicNames().add(clinicName);
            em.merge(healthUser);
        }
        return healthUser;
    }

    @Override
    public HealthUser create(HealthUser healthUser) {
        if (healthUser == null) {
            throw new ValidationException("HealthUser must not be null");
        }
        logger.info("Persisting HealthUser with CI=" + healthUser.getCi());
        em.persist(healthUser);
        return healthUser;
    }

    @Override
    public List<ClinicalDocumentDTO> findClinicalHistory(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user CI must not be blank");
        }

        String encodedHealthUserCi = URLEncoder.encode(healthUserCi, StandardCharsets.UTF_8);
        URI uri = URI.create(CLINICAL_HISTORY_BASE_URL + "/" + encodedHealthUserCi);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 404) {
                logger.log(Level.INFO, "Clinical history not found for health user: {0}", healthUserCi);
                return new ArrayList<>();
            }

            if (status != 200) {
                logger.log(Level.WARNING,
                        "Unexpected response when fetching clinical history for health user {0}: HTTP {1}. Response body: {2}",
                        new Object[] { healthUserCi, status, response.body() });
                throw new IllegalStateException("Failed to fetch clinical history: HTTP " + status);
            }

            return mapResponseToDtoList(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching clinical history", ex);
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error calling clinical history service", ex);
            throw new IllegalStateException("Unable to fetch clinical history data", ex);
        }
    }

    private List<ClinicalDocumentDTO> mapResponseToDtoList(String body) {
        if (body == null || body.isBlank()) {
            return new ArrayList<>();
        }

        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            JsonArray jsonArray = reader.readArray();
            List<ClinicalDocumentDTO> documents = new ArrayList<>();

            for (JsonValue value : jsonArray) {
                if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject json = value.asJsonObject();
                    ClinicalDocumentDTO dto = new ClinicalDocumentDTO();

                    if (json.containsKey("clinic") && !json.isNull("clinic")) {
                        JsonObject clinicJson = json.getJsonObject("clinic");
                        ClinicDTO clinicDto = new ClinicDTO();
                        clinicDto.setId(getString(clinicJson, "id"));
                        clinicDto.setName(getString(clinicJson, "name"));
                        dto.setClinic(clinicDto);
                    }

                    if (json.containsKey("healthWorker") && !json.isNull("healthWorker")) {
                        JsonObject hwJson = json.getJsonObject("healthWorker");
                        HealthWorkerDTO hwDto = new HealthWorkerDTO();

                        if (hwJson.containsKey("user") && !hwJson.isNull("user")) {
                            JsonObject userJson = hwJson.getJsonObject("user");
                            hwDto.setCi(getString(userJson, "ci"));
                            hwDto.setFirstName(getString(userJson, "firstName"));
                            hwDto.setLastName(getString(userJson, "lastName"));
                            hwDto.setEmail(getString(userJson, "email"));
                            hwDto.setPhone(getString(userJson, "phone"));
                            hwDto.setAddress(getString(userJson, "address"));

                            String dob = getString(userJson, "dateOfBirth");
                            if (dob != null && !dob.isBlank()) {
                                try {
                                    // Parse ISO 8601 DateTime with timezone and convert to LocalDate
                                    hwDto.setDateOfBirth(
                                            Instant.parse(dob).atZone(ZoneId.systemDefault()).toLocalDate());
                                } catch (DateTimeParseException ex) {
                                    logger.log(Level.WARNING, "Invalid dateOfBirth for health worker: " + dob, ex);
                                }
                            }
                        }

                        dto.setHealthWorker(hwDto);
                    }

                    dto.setTitle(getString(json, "title"));
                    dto.setDescription(getString(json, "description"));
                    dto.setContent(getString(json, "content"));
                    dto.setContentType(getString(json, "contentType"));
                    dto.setContentUrl(getString(json, "contentUrl"));

                    String createdAt = getString(json, "createdAt");
                    if (createdAt != null && !createdAt.isBlank()) {
                        try {
                            // Parse ISO 8601 DateTime with timezone and convert to LocalDate
                            dto.setCreatedAt(Instant.parse(createdAt).atZone(ZoneId.systemDefault()).toLocalDate());
                        } catch (DateTimeParseException ex) {
                            logger.log(Level.WARNING, "Invalid createdAt date: " + createdAt, ex);
                        }
                    }

                    String updatedAt = getString(json, "updatedAt");
                    if (updatedAt != null && !updatedAt.isBlank()) {
                        try {
                            // Parse ISO 8601 DateTime with timezone and convert to LocalDate
                            dto.setUpdatedAt(Instant.parse(updatedAt).atZone(ZoneId.systemDefault()).toLocalDate());
                        } catch (DateTimeParseException ex) {
                            logger.log(Level.WARNING, "Invalid updatedAt date: " + updatedAt, ex);
                        }
                    }

                    documents.add(dto);
                }
            }

            return documents;
        } catch (JsonException ex) {
            throw new IllegalStateException("Invalid JSON received for clinical history", ex);
        }
    }

    private String getString(JsonObject json, String key) {
        JsonValue value = json.get(key);
        if (value == null || value.getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }
        return json.getString(key, null);
    }
}
