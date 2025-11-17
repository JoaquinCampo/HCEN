package grupo12.practico.services.HealthUser;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryRequestDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.HealthUserAccessHistoryResponseDTO;
import grupo12.practico.models.ClinicalHistoryLog;
import grupo12.practico.models.HealthUser;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import grupo12.practico.services.PushNotificationSender.PushNotificationServiceLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.services.Logger.LoggerServiceLocal;
import java.util.ArrayList;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

@Stateless
@Local(HealthUserServiceLocal.class)
@Remote(HealthUserServiceRemote.class)
public class HealthUserServiceBean implements HealthUserServiceRemote {
    public HealthUserServiceBean() {
    }

    private static final Logger logger = Logger.getLogger(HealthUserServiceBean.class.getName());

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private AccessPolicyServiceLocal accessPolicyService;

    @EJB
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @EJB
    private NotificationTokenServiceLocal notificationTokenService;

    @EJB
    private PushNotificationServiceLocal pushNotificationService;

    @EJB
    private ClinicServiceLocal clinicService;

    @EJB
    private LoggerServiceLocal loggerService;

    @Override
    public PaginationDTO<HealthUserDTO> findAllHealthUsers(String clinicName, String name, String ci, Integer pageIndex,
            Integer pageSize) {
        int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<HealthUser> users = healthUserRepository.findAllHealthUsers(clinicName, name, ci, safePageIndex,
                safePageSize);
        long total = healthUserRepository.countHealthUsers(clinicName, name, ci);

        List<HealthUserDTO> userDTOs = users.stream()
                .map(this::mapHealthUserResponseToDTO)
                .collect(Collectors.toList());

        PaginationDTO<HealthUserDTO> paginationDTO = new PaginationDTO<>();
        paginationDTO.setItems(userDTOs);
        paginationDTO.setPageIndex(safePageIndex);
        paginationDTO.setPageSize(safePageSize);
        paginationDTO.setTotal(total);
        paginationDTO.setTotalPages((long) Math.ceil((double) total / safePageSize));
        paginationDTO.setHasNextPage(safePageIndex < paginationDTO.getTotalPages() - 1);
        paginationDTO.setHasPreviousPage(safePageIndex > 0);

        return paginationDTO;
    }

    @Override
    public HealthUserDTO createHealthUser(AddHealthUserDTO addHealthUserDTO) {
        validateAddHealthUserDTO(addHealthUserDTO);

        HealthUser healthUser = new HealthUser();
        healthUser.setCi(addHealthUserDTO.getCi());
        healthUser.setFirstName(addHealthUserDTO.getFirstName());
        healthUser.setLastName(addHealthUserDTO.getLastName());
        healthUser.setGender(addHealthUserDTO.getGender());
        healthUser.setEmail(addHealthUserDTO.getEmail());
        healthUser.setPhone(addHealthUserDTO.getPhone());
        healthUser.setAddress(addHealthUserDTO.getAddress());
        healthUser.setDateOfBirth(addHealthUserDTO.getDateOfBirth());
        healthUser.setClinicNames(addHealthUserDTO.getClinicNames());

        HealthUser created = healthUserRepository.createHealthUser(healthUser);

        // Log health user creation
        for (String clinicName : created.getClinicNames()) {
            loggerService.logHealthUserCreated(created.getCi(), clinicName);
        }

        return mapHealthUserResponseToDTO(created);
    }

    @Override
    public HealthUserDTO findHealthUserByCi(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user CI is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        return healthUser != null ? mapHealthUserResponseToDTO(healthUser) : null;
    }

    @Override
    public HealthUserDTO linkClinicToHealthUser(String healthUserId, String clinicName) {
        if (healthUserId == null || healthUserId.isBlank() || clinicName == null || clinicName.isBlank()) {
            throw new ValidationException("Health user ID and clinic name are required");
        }

        HealthUser healthUser = healthUserRepository.linkClinicToHealthUser(healthUserId, clinicName);

        // Log clinic link
        loggerService.logHealthUserClinicLinked(healthUser.getCi(), clinicName);

        return mapHealthUserResponseToDTO(healthUser);
    }

    @Override
    public ClinicalHistoryResponseDTO findHealthUserClinicalHistory(ClinicalHistoryRequestDTO request) {

        if (request == null) {
            throw new ValidationException("Clinical history request is required");
        }

        if (request.getHealthUserCi() == null || request.getHealthUserCi().isBlank()) {
            throw new ValidationException("Health user CI is required");
        }

        boolean isHealthUser = request.getHealthWorkerCi() == null && request.getClinicName() == null
                && request.getSpecialtyNames() == null;

        boolean hasClinicAccess = !isHealthUser
                && accessPolicyService.hasClinicAccess(request.getHealthUserCi(), request.getClinicName());
        boolean hasWorkerAccess = !isHealthUser
                && accessPolicyService.hasHealthWorkerAccess(request.getHealthUserCi(), request.getHealthWorkerCi());
        boolean hasSpecialtyAccess = !isHealthUser
                && accessPolicyService.hasSpecialtyAccess(request.getHealthUserCi(), request.getSpecialtyNames());

        if (!isHealthUser && !hasClinicAccess && !hasWorkerAccess && !hasSpecialtyAccess) {
            throw new ValidationException(
                    "Access denied to clinical history for health user CI: " + request.getHealthUserCi());
        }

        List<ClinicalDocumentDTO> documents = healthUserRepository
                .findHealthUserClinicalHistory(request.getHealthUserCi());

        ClinicalHistoryResponseDTO response = new ClinicalHistoryResponseDTO();
        HealthUserDTO healthUserDTO = findHealthUserByCi(request.getHealthUserCi());
        response.setHealthUser(healthUserDTO);
        response.setDocuments(documents);

        // Log clinical history access
        if (isHealthUser) {
            // Self-access by health user
            loggerService.logClinicalHistoryAccessBySelf(request.getHealthUserCi());
        } else {
            // Access by health worker
            try {
                String accessType = hasClinicAccess ? "BY_CLINIC"
                        : hasWorkerAccess ? "BY_HEALTH_WORKER" : hasSpecialtyAccess ? "BY_SPECIALTY" : "UNKNOWN";
                loggerService.logClinicalHistoryAccessByHealthWorker(
                        request.getHealthUserCi(),
                        request.getHealthWorkerCi(),
                        request.getClinicName(),
                        request.getSpecialtyNames(),
                        accessType);
            } catch (Exception e) {
                logger.warning("Failed to log clinical history access by health worker: " + e.getMessage());
            }
        }

        return response;
    }

    @Override
    public HealthUserAccessHistoryResponseDTO findHealthUserAccessHistory(String healthUserCi, Integer pageIndex,
            Integer pageSize) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user CI is required");
        }

        HealthUserDTO healthUser = findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found for CI: " + healthUserCi);
        }

        PaginationDTO<ClinicalHistoryLog> logsPage = loggerService.getClinicalHistoryLogs(healthUserCi, null,
                pageIndex, pageSize);
        List<ClinicalHistoryAccessLogResponseDTO> accessLogs = logsPage != null && logsPage.getItems() != null
                ? logsPage.getItems().stream()
                        .map(this::mapClinicalHistoryLogToDTO)
                        .collect(Collectors.toList())
                : java.util.Collections.emptyList();

        HealthUserAccessHistoryResponseDTO response = new HealthUserAccessHistoryResponseDTO();
        response.setHealthUser(healthUser);
        response.setAccessHistory(accessLogs);
        return response;
    }

    @Override
    public HealthUserDTO findHealthUserById(String healthUserId) {
        if (healthUserId == null || healthUserId.isBlank()) {
            throw new ValidationException("Health user ID is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserById(healthUserId);
        return healthUser != null ? mapHealthUserResponseToDTO(healthUser) : null;
    }

    private HealthUserDTO mapHealthUserResponseToDTO(HealthUser healthUser) {
        if (healthUser == null) {
            return null;
        }

        HealthUserDTO dto = new HealthUserDTO();
        dto.setId(healthUser.getId());
        dto.setCi(healthUser.getCi());
        dto.setFirstName(healthUser.getFirstName());
        dto.setLastName(healthUser.getLastName());
        dto.setGender(healthUser.getGender());
        dto.setEmail(healthUser.getEmail());
        dto.setPhone(healthUser.getPhone());
        dto.setAddress(healthUser.getAddress());
        dto.setDateOfBirth(healthUser.getDateOfBirth());
        dto.setCreatedAt(healthUser.getCreatedAt());
        dto.setUpdatedAt(healthUser.getUpdatedAt());

        List<ClinicDTO> clinics = new ArrayList<>();
        if (healthUser.getClinicNames() != null) {
            for (String clinicName : healthUser.getClinicNames()) {
                try {
                    ClinicDTO clinicDTO = clinicService.findClinicByName(clinicName);
                    if (clinicDTO != null) {
                        clinics.add(clinicDTO);
                    }
                } catch (Exception e) {
                    logger.warning("Failed to fetch clinic with name: " + clinicName + ". Error: " + e.getMessage());
                }
            }
        }
        dto.setClinics(clinics);

        return dto;
    }

    private void validateAddHealthUserDTO(AddHealthUserDTO addHealthUserDTO) {
        if (addHealthUserDTO == null) {
            throw new ValidationException("Health user data must not be null");
        }
        if (addHealthUserDTO.getFirstName() == null || addHealthUserDTO.getFirstName().isBlank()) {
            throw new ValidationException("Health user first name is required");
        }
        if (addHealthUserDTO.getLastName() == null || addHealthUserDTO.getLastName().isBlank()) {
            throw new ValidationException("Health user last name is required");
        }
        if (addHealthUserDTO.getCi() == null || addHealthUserDTO.getCi().isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (addHealthUserDTO.getClinicNames() == null || addHealthUserDTO.getClinicNames().isEmpty()) {
            throw new ValidationException("Health user clinic names are required");
        }
        if (addHealthUserDTO.getGender() == null) {
            throw new ValidationException("Health user gender is required");
        }
        if (addHealthUserDTO.getEmail() == null || addHealthUserDTO.getEmail().isBlank()) {
            throw new ValidationException("Health user email is required");
        }
    }

    private ClinicalHistoryAccessLogResponseDTO mapClinicalHistoryLogToDTO(ClinicalHistoryLog log) {
        ClinicalHistoryAccessLogResponseDTO dto = new ClinicalHistoryAccessLogResponseDTO();
        dto.setId(log.getId());
        dto.setHealthUserCi(log.getHealthUserCi());
        dto.setHealthWorkerCi("HEALTH_WORKER".equalsIgnoreCase(log.getAccessorType()) ? log.getAccessorCi() : null);
        dto.setClinicName(log.getClinicName());
        dto.setRequestedAt(log.getTimestamp());
        dto.setViewed("HEALTH_USER".equalsIgnoreCase(log.getAccessorType()));
        dto.setDecisionReason(log.getAccessType());
        return dto;
    }
}
