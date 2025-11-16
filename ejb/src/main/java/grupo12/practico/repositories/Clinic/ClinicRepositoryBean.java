package grupo12.practico.repositories.Clinic;

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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import grupo12.practico.dtos.Clinic.AddClinicDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.repositories.NodosPerifericosConfig;

@Stateless
@Local(ClinicRepositoryLocal.class)
@Remote(ClinicRepositoryRemote.class)
public class ClinicRepositoryBean implements ClinicRepositoryRemote {

    private static final Logger logger = Logger.getLogger(ClinicRepositoryBean.class.getName());

    @EJB
    private NodosPerifericosConfig config;

    private final HttpClient httpClient;

    public ClinicRepositoryBean() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @Override
    public ClinicDTO createClinic(AddClinicDTO addClinicDTO) {
        var clinicAdminBuilder = Json.createObjectBuilder()
                .add("ci", addClinicDTO.getClinicAdmin().getCi())
                .add("firstName", addClinicDTO.getClinicAdmin().getFirstName())
                .add("lastName", addClinicDTO.getClinicAdmin().getLastName())
                .add("email", addClinicDTO.getClinicAdmin().getEmail());
        
        if (addClinicDTO.getClinicAdmin().getPhone() != null) {
            clinicAdminBuilder.add("phone", addClinicDTO.getClinicAdmin().getPhone());
        }
        
        if (addClinicDTO.getClinicAdmin().getAddress() != null) {
            clinicAdminBuilder.add("address", addClinicDTO.getClinicAdmin().getAddress());
        }
        
        if (addClinicDTO.getClinicAdmin().getDateOfBirth() != null) {
            clinicAdminBuilder.add("dateOfBirth", addClinicDTO.getClinicAdmin().getDateOfBirth().toString());
        }
        
        String jsonPayload = Json.createObjectBuilder()
                .add("name", addClinicDTO.getName())
                .add("email", addClinicDTO.getEmail())
                .add("phone", addClinicDTO.getPhone())
                .add("address", addClinicDTO.getAddress())
                .add("providerName", addClinicDTO.getProviderName())
                .add("clinicAdmin", clinicAdminBuilder.build())
                .build()
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getClinicsApiUrl()))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 201) {
                logger.log(Level.INFO, "Clinic created successfully");
                return mapClinicResponseToDto(response.body());
            } else {
                logger.log(Level.WARNING, "Failed to create clinic: HTTP {0}, Body: {1}",
                        new Object[] { status, response.body() });
                throw new IllegalStateException("Failed to create clinic: HTTP " + status);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while creating clinic", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to create clinic", ex);
        }
    }

    @Override
    public ClinicDTO findClinicByName(String name) {
        String encodedName = encodePathSegment(name);
        URI uri = URI.create(config.getClinicsApiUrl() + "/" + encodedName);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status == 404) {
                logger.log(Level.INFO, "Clinic not found: {0}", name);
                return null;
            }

            if (status != 200) {
                logger.log(Level.WARNING, "Unexpected response when fetching clinic {0}: HTTP {1}",
                        new Object[] { name, status });
                throw new IllegalStateException("Failed to fetch clinic: HTTP " + status);
            }

            return mapClinicResponseToDto(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching clinic data", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to fetch clinic data", ex);
        }
    }

    @Override
    public List<ClinicDTO> findAllClinics(String providerName) {
        URI uri;
        if (providerName == null || providerName.isBlank()) {
            uri = URI.create(config.getClinicsApiUrl());
        } else {
            String encodedProviderName = encodePathSegment(providerName);
            uri = URI.create(config.getClinicsApiUrl() + "?providerName=" + encodedProviderName);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();

            if (status != 200) {
                logger.log(Level.WARNING, "Unexpected response when fetching all clinics: HTTP {0}", status);
                throw new IllegalStateException("Failed to fetch clinics: HTTP " + status);
            }
            
            return mapFindAllClinicsResponseToListDto(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while fetching clinics data", ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Network error connecting to {0}: {1}",
                    new Object[] { config.getClinicsApiUrl(), ex.getMessage() });
            throw new IllegalStateException("Unable to fetch clinics data", ex);
        }
    }

    private ClinicDTO mapClinicResponseToDto(String body) {
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
            if (createdAt != null && !createdAt.isBlank()) {
                try {
                    dto.setCreatedAt(Instant.parse(createdAt).atZone(ZoneId.systemDefault()).toLocalDate());
                } catch (DateTimeParseException ex) {
                    logger.log(Level.WARNING, "Invalid createdAt received for clinic: " + createdAt, ex);
                }
            }

            String updatedAt = getString(json, "updatedAt");
            if (updatedAt != null && !updatedAt.isBlank()) {
                try {
                    dto.setUpdatedAt(Instant.parse(updatedAt).atZone(ZoneId.systemDefault()).toLocalDate());
                } catch (DateTimeParseException ex) {
                    logger.log(Level.WARNING, "Invalid updatedAt received for clinic: " + updatedAt, ex);
                }
            }

            
            JsonArray healthWorkersArray = json.getJsonArray("healthWorkers");
            if (healthWorkersArray != null) {
                List<HealthWorkerDTO> healthWorkers = new ArrayList<>();
                for (JsonValue value : healthWorkersArray) {
                    if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                        JsonObject hwJson = value.asJsonObject();
                        HealthWorkerDTO hw = new HealthWorkerDTO();
                        hw.setCi(getString(hwJson, "ci"));
                        hw.setFirstName(getString(hwJson, "firstName"));
                        hw.setLastName(getString(hwJson, "lastName"));
                        hw.setEmail(getString(hwJson, "email"));
                        hw.setPhone(getString(hwJson, "phone"));
                        hw.setAddress(getString(hwJson, "address"));
                        healthWorkers.add(hw);
                    }
                }
                dto.setHealthWorkers(healthWorkers);
            }

            return dto;
        } catch (JsonException ex) {
            throw new IllegalStateException("Invalid JSON received for clinic", ex);
        }
    }

    private List<ClinicDTO> mapFindAllClinicsResponseToListDto(String body) {
        if (body == null || body.isBlank()) {
            return Collections.emptyList();
        }

        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            JsonArray jsonArray = reader.readArray();
            List<ClinicDTO> clinics = new ArrayList<>();

            for (JsonValue value : jsonArray) {
                if (value.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject json = value.asJsonObject();
                    ClinicDTO dto = new ClinicDTO();
                    dto.setId(getString(json, "id"));
                    dto.setName(getString(json, "name"));
                    dto.setEmail(getString(json, "email"));
                    dto.setPhone(getString(json, "phone"));
                    dto.setAddress(getString(json, "address"));

                    String createdAt = getString(json, "createdAt");
                    if (createdAt != null && !createdAt.isBlank()) {
                        try {
                            dto.setCreatedAt(Instant.parse(createdAt).atZone(ZoneId.systemDefault()).toLocalDate());
                        } catch (DateTimeParseException ex) {
                            logger.log(Level.WARNING, "Invalid createdAt received for clinic: " + createdAt, ex);
                        }
                    }

                    String updatedAt = getString(json, "updatedAt");
                    if (updatedAt != null && !updatedAt.isBlank()) {
                        try {
                            dto.setUpdatedAt(Instant.parse(updatedAt).atZone(ZoneId.systemDefault()).toLocalDate());
                        } catch (DateTimeParseException ex) {
                            logger.log(Level.WARNING, "Invalid updatedAt received for clinic: " + updatedAt, ex);
                        }
                    }

                    JsonArray healthWorkersArray = json.getJsonArray("healthWorkers");
                    if (healthWorkersArray != null) {
                        List<HealthWorkerDTO> healthWorkers = new ArrayList<>();
                        for (JsonValue hwValue : healthWorkersArray) {
                            if (hwValue.getValueType() == JsonValue.ValueType.OBJECT) {
                                JsonObject hwJson = hwValue.asJsonObject();
                                HealthWorkerDTO hw = new HealthWorkerDTO();
                                hw.setCi(getString(hwJson, "ci"));
                                hw.setFirstName(getString(hwJson, "firstName"));
                                hw.setLastName(getString(hwJson, "lastName"));
                                hw.setEmail(getString(hwJson, "email"));
                                hw.setPhone(getString(hwJson, "phone"));
                                hw.setAddress(getString(hwJson, "address"));
                                healthWorkers.add(hw);
                            }
                        }
                        dto.setHealthWorkers(healthWorkers);
                    }

                    clinics.add(dto);
                }
            }

            return clinics;
        } catch (JsonException ex) {
            throw new IllegalStateException("Invalid JSON received for clinics list", ex);
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
