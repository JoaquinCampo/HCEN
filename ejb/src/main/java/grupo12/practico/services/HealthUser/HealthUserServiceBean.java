package grupo12.practico.services.HealthUser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.ClinicalHistoryDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import grupo12.practico.repositories.ClinicAccessPolicy.ClinicAccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthWorkerAccessPolicy.HealthWorkerAccessPolicyRepositoryLocal;

@Stateless
@Local(HealthUserServiceLocal.class)
@Remote(HealthUserServiceRemote.class)
public class HealthUserServiceBean implements HealthUserServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(HealthUserServiceBean.class.getName());
    private static final String CLINICAL_HISTORY_ENDPOINT_TEMPLATE = "http://localhost:3000/api/clinical-history/%s";

    @EJB
    private ClinicAccessPolicyRepositoryLocal clinicAccessPolicyRepository;
    @EJB
    private HealthWorkerAccessPolicyRepositoryLocal healthWorkerAccessPolicyRepository;
    private final HttpClient httpClient;

    public HealthUserServiceBean() {
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

    @EJB
    private HealthUserRepositoryLocal userRepository;

    @Override
    public List<HealthUserDTO> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize) {
        return userRepository.findAll(clinicName, name, ci, pageIndex, pageSize).stream()
                .map(HealthUser::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HealthUserDTO add(AddHealthUserDTO addHealthUserDTO) {
        validateCreateUserDTO(addHealthUserDTO);
        HealthUser healthUser = createHealthUserFromDTO(addHealthUserDTO);
        userRepository.add(healthUser);
        return healthUser.toDto();
    }

    @Override
    public HealthUserDTO addClinicToHealthUser(String healthUserId, String clinicName) {
        return userRepository.addClinicToHealthUser(healthUserId, clinicName).toDto();
    }

    private void validateCreateUserDTO(AddHealthUserDTO addHealthUserDTO) {
        if (addHealthUserDTO == null) {
            throw new ValidationException("User data must not be null");
        }
        if (isBlank(addHealthUserDTO.getFirstName()) || isBlank(addHealthUserDTO.getLastName())) {
            throw new ValidationException("User first name and last name are required");
        }
        if (isBlank(addHealthUserDTO.getCi())) {
            throw new ValidationException("User document is required");
        }
    }

    @Override
    public ClinicalHistoryDTO getClinicalHistory(String healthUserCi, String clinicName, String healthWorkerCi) {
        if (isBlank(healthUserCi)) {
            throw new ValidationException("Health user CI must not be null or empty");
        }
        HealthUser healthUser = userRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }
        boolean hasClinicPolicy = clinicName != null && !clinicName.isEmpty()
                && clinicAccessPolicyRepository
                        .findByHealthUserAndClinic(healthUser.getId(), clinicName)
                        .isPresent();
        boolean hasHealthWorkerPolicy = healthWorkerCi != null && !healthWorkerCi.isEmpty()
                && healthWorkerAccessPolicyRepository
                        .findByHealthUserAndHealthWorker(healthUser.getId(), healthWorkerCi)
                        .isPresent();
        if (!hasClinicPolicy && !hasHealthWorkerPolicy) {
            throw new ValidationException("Access denied to clinical history");
        }
        try {
            String encodedHealthUserId = URLEncoder.encode(healthUser.getId(), StandardCharsets.UTF_8);
            String url = String.format(CLINICAL_HISTORY_ENDPOINT_TEMPLATE, encodedHealthUserId);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                String message = "Failed to retrieve clinical history. Remote service returned status "
                        + response.statusCode();
                LOGGER.log(Level.WARNING, "{0}. Response body: {1}", new Object[] { message, response.body() });
                throw new IllegalStateException(message);
            }
            ClinicalHistoryDTO dto = new ClinicalHistoryDTO();
            dto.setHealthUserId(healthUser.getId());
            dto.setPayload(response.body());
            return dto;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Clinical history retrieval interrupted", e);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error calling clinical history service", e);
            throw new IllegalStateException("Error calling clinical history service", e);
        }
    }

    private HealthUser createHealthUserFromDTO(AddHealthUserDTO dto) {
        HealthUser user = new HealthUser();

        user.setCi(dto.getCi());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setGender(dto.getGender());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setDateOfBirth(dto.getDateOfBirth());

        return user;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
