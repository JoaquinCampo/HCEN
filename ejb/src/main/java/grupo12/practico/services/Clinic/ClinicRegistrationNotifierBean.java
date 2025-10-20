package grupo12.practico.services.Clinic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import grupo12.practico.dtos.Clinic.ClinicAdminInfoDTO;
import grupo12.practico.models.Clinic;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

@Stateless
public class ClinicRegistrationNotifierBean implements ClinicRegistrationNotifierLocal {

    private static final Logger LOGGER = Logger.getLogger(ClinicRegistrationNotifierBean.class.getName());
    private static final String PROPERTY_ENDPOINT = "app.external.clinicRegistrationUrl";
    private static final String ENV_ENDPOINT = "CLINIC_REGISTRATION_ENDPOINT";
    private static final String DEFAULT_ENDPOINT = "http://localhost:8081/api/clinics";

    private HttpClient httpClient;
    private String endpoint;

    @PostConstruct
    protected void init() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.endpoint = resolveEndpoint();
        LOGGER.info(() -> "Clinic registration notifier using endpoint: " + endpoint);
    }

    @Override
    public void notifyClinicCreated(Clinic clinic, ClinicAdminInfoDTO admin) {
        if (clinic == null || admin == null) {
            LOGGER.warning("Skipping external notification: clinic or admin information is missing");
            return;
        }

        try {
            String payload = buildPayload(clinic, admin);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                LOGGER.fine(() -> "External clinic registration succeeded with status " + response.statusCode());
            } else {
                LOGGER.warning(() -> String.format(
                        "External clinic registration returned status %d. Response body: %s",
                        response.statusCode(), response.body()));
            }
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to notify external service about clinic registration", ex);
        }
    }

    private String buildPayload(Clinic clinic, ClinicAdminInfoDTO admin) {
        JsonObjectBuilder clinicBuilder = Json.createObjectBuilder()
                .add("name", clinic.getName())
                .add("email", clinic.getEmail() == null ? "" : clinic.getEmail())
                .add("phone", clinic.getPhone() == null ? "" : clinic.getPhone())
                .add("address", clinic.getAddress());

        JsonObjectBuilder adminBuilder = Json.createObjectBuilder()
                .add("name", admin.getName())
                .add("email", admin.getEmail());

        if (admin.getPhone() != null && !admin.getPhone().isBlank()) {
            adminBuilder.add("phone", admin.getPhone());
        }

        clinicBuilder.add("clinicAdmin", adminBuilder);
        return clinicBuilder.build().toString();
    }

    private String resolveEndpoint() {
        String configured = System.getProperty(PROPERTY_ENDPOINT);
        if (configured == null || configured.isBlank()) {
            configured = System.getenv(ENV_ENDPOINT);
        }
        return (configured == null || configured.isBlank()) ? DEFAULT_ENDPOINT : configured;
    }
}
