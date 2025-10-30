package grupo12.practico.services.HealthWorker;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.validation.ValidationException;

@Stateless
@Local(HealthWorkerServiceLocal.class)
@Remote(HealthWorkerServiceRemote.class)
public class HealthWorkerServiceBean implements HealthWorkerServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(HealthWorkerServiceBean.class.getName());
    private static final String BASE_URL = "http://localhost:3000/api/clinics";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public List<HealthWorkerDTO> findAll() {
        return Collections.emptyList();
    }

    @Override
    public HealthWorkerDTO findById(String id) {
        return null;
    }

    @Override
    public List<HealthWorkerDTO> findByName(String name) {
        return Collections.emptyList();
    }

    @Override
    public HealthWorkerDTO findByClinicAndCi(String clinicName, String healthWorkerCi) {
        if (clinicName == null || clinicName.isBlank()) {
            throw new ValidationException("Clinic name must not be blank");
        }
        if (healthWorkerCi == null || healthWorkerCi.isBlank()) {
            throw new ValidationException("Health worker CI must not be blank");
        }

        String encodedClinic = encodePathSegment(clinicName);
        String encodedCi = encodePathSegment(healthWorkerCi);
        URI uri = URI.create(BASE_URL + "/" + encodedClinic + "/healthWorker/" + encodedCi);

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
                LOGGER.log(Level.WARNING, "Unexpected response when fetching health worker {0} at clinic {1}: HTTP {2}",
                        new Object[] { healthWorkerCi, clinicName, status });
                throw new IllegalStateException("Failed to fetch health worker: HTTP " + status);
            }

            return mapResponseToDto(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching health worker data", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to fetch health worker data", ex);
        }
    }

    private HealthWorkerDTO mapResponseToDto(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }

        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            JsonObject json = reader.readObject();

            HealthWorkerDTO dto = new HealthWorkerDTO();
            dto.setCi(getString(json, "ci"));
            dto.setFirstName(getString(json, "firstName"));
            dto.setLastName(getString(json, "lastName"));
            dto.setEmail(getString(json, "email"));
            dto.setPhone(getString(json, "phone"));
            dto.setAddress(getString(json, "address"));

            String dob = getString(json, "dateOfBirth");
            if (dob != null && !dob.isBlank()) {
                try {
                    dto.setDateOfBirth(LocalDate.parse(dob));
                } catch (DateTimeParseException ex) {
                    LOGGER.log(Level.WARNING, "Invalid dateOfBirth received for health worker: " + dob, ex);
                }
            }

            return dto;
        } catch (JsonException ex) {
            throw new IllegalStateException("Invalid JSON received for health worker", ex);
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
}
