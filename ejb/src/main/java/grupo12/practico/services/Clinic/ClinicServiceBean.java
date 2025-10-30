package grupo12.practico.services.Clinic;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicAdminDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Stateless
@Local(ClinicServiceLocal.class)
@Remote(ClinicServiceRemote.class)
public class ClinicServiceBean implements ClinicServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(ClinicServiceBean.class.getName());
    private static final String EXTERNAL_BASE_URL = "http://localhost:3000/api/clinics";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @EJB
    private ClinicRegistrationNotifierLocal registrationNotifier;

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @Override
    public ClinicDTO addClinic(AddClinicDTO addclinicDTO) {
        validateClinic(addclinicDTO);
        Clinic clinic = new Clinic();
        clinic.setName(addclinicDTO.getName());
        clinic.setEmail(addclinicDTO.getEmail());
        clinic.setPhone(addclinicDTO.getPhone());
        clinic.setAddress(addclinicDTO.getAddress());
        Clinic persisted = repository.add(clinic);
        registrationNotifier.notifyClinicCreated(persisted, addclinicDTO.getClinicAdmin());
        return persisted.toDto();
    }

    @Override
    public List<ClinicDTO> findAll() {
        return repository.findAll().stream()
                .map(Clinic::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ClinicDTO findById(String id) {
        return repository.findById(id).toDto();
    }

    @Override
    public List<ClinicDTO> findByName(String name) {
        return repository.findByName(name).stream()
                .map(Clinic::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public String linkHealthUserToClinic(String clinicName, String healthUserDocument) {
        if (isBlank(clinicName)) {
            throw new ValidationException("Clinic name is required");
        }
        if (isBlank(healthUserDocument)) {
            throw new ValidationException("Health user document is required");
        }

        String normalizedClinicName = clinicName.trim();
        String normalizedDocument = healthUserDocument.trim();

        List<Clinic> matches = repository.findByName(normalizedClinicName);
        if (matches == null || matches.isEmpty()) {
            matches = repository.findAll();
        }
        String normalizedClinicKey = normalizeForComparison(normalizedClinicName);
        Clinic clinic = matches.stream()
                .filter(c -> normalizedClinicKey.equals(normalizeForComparison(c.getName())))
                .findFirst()
                .orElse(null);

        if (clinic == null) {
            throw new EntityNotFoundException("Clinic not found with name: " + normalizedClinicName);
        }

        HealthUser healthUser = healthUserRepository.findByDocument(normalizedDocument);
        if (healthUser == null) {
            throw new EntityNotFoundException("Health user not found with document: " + normalizedDocument);
        }

        if (healthUser.getClinics() == null) {
            healthUser.setClinics(new HashSet<>());
        }
        if (healthUser.getClinics().contains(clinic)) {
            return "Health user is already linked to the clinic";
        }

        healthUser.getClinics().add(clinic);
        if (clinic.getHealthUsers() == null) {
            clinic.setHealthUsers(new HashSet<>());
        }
        clinic.getHealthUsers().add(healthUser);

        return "Health user linked to clinic successfully";
    }

    @Override
    public ClinicDTO findExternalClinicByName(String clinicName) {
        if (isBlank(clinicName)) {
            throw new ValidationException("Clinic name is required");
        }

        String normalizedName = clinicName.trim();
        String encodedName = encodePathSegment(normalizedName);
        URI uri = URI.create(EXTERNAL_BASE_URL + "/" + encodedName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 404) {
                return null;
            }

            if (status != 200) {
                LOGGER.log(Level.WARNING, "Unexpected response when fetching clinic {0}: HTTP {1}",
                        new Object[] { normalizedName, status });
                throw new IllegalStateException("Failed to fetch clinic: HTTP " + status);
            }

            return mapExternalClinic(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching clinic data", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to fetch clinic data", ex);
        }
    }

    private ClinicDTO mapExternalClinic(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }

        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            JsonObject json = reader.readObject();

            ClinicDTO dto = new ClinicDTO();
            dto.setId(getString(json, "id"));
            dto.setName(getString(json, "name"));
            dto.setEmail(getString(json, "email"));
            dto.setPhone(getString(json, "phone"));
            dto.setAddress(getString(json, "address"));

            String createdAt = getString(json, "createdAt");
            if (createdAt != null) {
                LocalDate parsed = parseDate(createdAt, "createdAt");
                if (parsed != null) {
                    dto.setCreatedAt(parsed);
                }
            }

            String updatedAt = getString(json, "updatedAt");
            if (updatedAt != null) {
                LocalDate parsed = parseDate(updatedAt, "updatedAt");
                if (parsed != null) {
                    dto.setUpdatedAt(parsed);
                }
            }

            return dto;
        } catch (JsonException ex) {
            throw new IllegalStateException("Invalid JSON received for clinic", ex);
        }
    }

    private LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            LOGGER.log(Level.WARNING, "Invalid {0} received for clinic: {1}", new Object[] { fieldName, value });
            return null;
        }
    }

    private String getString(JsonObject json, String key) {
        JsonValue value = json.get(key);
        if (value == null || value.getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }
        return json.getString(key, null);
    }

    private String encodePathSegment(String segment) {
        String encoded = URLEncoder.encode(segment, StandardCharsets.UTF_8);
        return encoded.replace("+", "%20");
    }

    private void validateClinic(AddClinicDTO addClinicDTO) {
        if (addClinicDTO == null) {
            throw new ValidationException("Clinic must not be null");
        }
        if (isBlank(addClinicDTO.getName())) {
            throw new ValidationException("Clinic name is required");
        }
        if (isBlank(addClinicDTO.getEmail())) {
            throw new ValidationException("Clinic email is required");
        }
        if (isBlank(addClinicDTO.getPhone())) {
            throw new ValidationException("Clinic phone is required");
        }
        if (isBlank(addClinicDTO.getAddress())) {
            throw new ValidationException("Address is required");
        }

        ClinicAdminDTO admin = addClinicDTO.getClinicAdmin();
        if (admin == null) {
            throw new ValidationException("Clinic admin information is required");
        }
        if (isBlank(admin.getName())) {
            throw new ValidationException("Clinic admin name is required");
        }
        if (isBlank(admin.getEmail())) {
            throw new ValidationException("Clinic admin email is required");
        }

    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeForComparison(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim().toLowerCase();
        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

}
