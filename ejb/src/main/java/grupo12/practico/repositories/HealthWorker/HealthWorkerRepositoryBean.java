package grupo12.practico.repositories.HealthWorker;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.validation.ValidationException;

import grupo12.practico.repositories.NodosPerifericosConfig;

@Stateless
@Local(HealthWorkerRepositoryLocal.class)
@Remote(HealthWorkerRepositoryRemote.class)
public class HealthWorkerRepositoryBean implements HealthWorkerRepositoryRemote {

    private static final Logger logger = Logger.getLogger(HealthWorkerRepositoryBean.class.getName());

    @EJB
    private NodosPerifericosConfig config;

    private final HttpClient httpClient;

    public HealthWorkerRepositoryBean() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
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
        URI uri = URI.create(config.getClinicsApiUrl() + "/" + encodedClinic + "/health-worker/" + encodedCi);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 404) {
                logger.log(Level.INFO, "Health worker not found: {0} at clinic {1}",
                        new Object[] { healthWorkerCi, clinicName });
                return null;
            }

            if (status != 200) {
                logger.log(Level.WARNING, "Unexpected response when fetching health worker {0} at clinic {1}: HTTP {2}",
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

            if (json.containsKey("user") && !json.isNull("user")) {
                JsonObject userJson = json.getJsonObject("user");
                dto.setCi(getString(userJson, "ci"));
                dto.setFirstName(getString(userJson, "firstName"));
                dto.setLastName(getString(userJson, "lastName"));
                dto.setEmail(getString(userJson, "email"));
                dto.setPhone(getString(userJson, "phone"));
                dto.setAddress(getString(userJson, "address"));

                String dob = getString(userJson, "dateOfBirth");
                if (dob != null && !dob.isBlank()) {
                    try {
                        dto.setDateOfBirth(LocalDate.parse(dob));
                    } catch (DateTimeParseException ex) {
                        throw new IllegalStateException("Invalid dateOfBirth received for health worker: " + dob, ex);
                    }
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
