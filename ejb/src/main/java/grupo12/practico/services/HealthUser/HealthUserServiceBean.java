package grupo12.practico.services.HealthUser;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.ClinicalDocumentDTO;
import grupo12.practico.dtos.HealthUser.ClinicalHistoryDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.NotificationType;
import grupo12.practico.repositories.AccessPolicy.AccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import grupo12.practico.services.PushNotificationSender.PushNotificationServiceLocal;
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
    private AccessPolicyRepositoryLocal accessPolicyRepository;

    @EJB
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @EJB
    private NotificationTokenServiceLocal notificationTokenService;

    @EJB
    private PushNotificationServiceLocal pushNotificationService;

    @Override
    public PaginationDTO<HealthUserDTO> findAll(String clinicName, String name, String ci, Integer pageIndex,
            Integer pageSize) {
        int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
        int safePageSize = pageSize != null && pageSize > 0 ? pageSize : 20;

        List<HealthUser> users = healthUserRepository.findAll(clinicName, name, ci, safePageIndex, safePageSize);
        long total = healthUserRepository.count(clinicName, name, ci);

        List<HealthUserDTO> userDTOs = users.stream()
                .map(HealthUser::toDto)
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
    public HealthUserDTO create(AddHealthUserDTO addHealthUserDTO) {
        logger.info("HealthUserServiceBean.create called with CI="
                + (addHealthUserDTO != null ? addHealthUserDTO.getCi() : "<null>"));
        validateCreateUserDTO(addHealthUserDTO);

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

        HealthUser created = healthUserRepository.create(healthUser);
        logger.info("Health user persisted with id=" + (created != null ? created.getId() : "<null>"));
        return created.toDto();
    }

    @Override
    public HealthUserDTO findByCi(String healthUserCi) {
        return healthUserRepository.findByCi(healthUserCi).toDto();
    }

    @Override
    public HealthUserDTO linkClinicToHealthUser(String healthUserId, String clinicName) {
        return healthUserRepository.linkClinicToHealthUser(healthUserId, clinicName).toDto();
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
    public HealthUserDTO findById(String healthUserId) {
        return healthUserRepository.findById(healthUserId).toDto();
    }

    @Override
    public ClinicalHistoryDTO findClinicalHistory(String healthUserCi, String clinicName, String healthWorkerCi) {
        if (isBlank(healthUserCi)) {
            throw new ValidationException("Health user CI must not be null or empty");
        }
        if (isBlank(clinicName)) {
            throw new ValidationException("Clinic name must not be null or empty");
        }
        if (isBlank(healthWorkerCi)) {
            throw new ValidationException("Health worker CI must not be null or empty");
        }

        HealthUser healthUser = healthUserRepository.findByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        boolean hasClinicPolicy = accessPolicyRepository
                .findAllClinicAccessPolicies(healthUser.getId())
                .stream()
                .anyMatch(policy -> policy.getClinicName().equals(clinicName));

        boolean hasHealthWorkerPolicy = accessPolicyRepository
                .findAllHealthWorkerAccessPolicies(healthUser.getId())
                .stream()
                .anyMatch(policy -> policy.getHealthWorkerCi().equals(healthWorkerCi)
                        && policy.getClinicName().equals(clinicName));

        if (!hasClinicPolicy && !hasHealthWorkerPolicy) {
            throw new ValidationException("Access denied to clinical history");
        }

        List<ClinicalDocumentDTO> clinicalDocuments = healthUserRepository.findClinicalHistory(healthUserCi);

        // Send notification to health user about clinical history access
        try {
            boolean isSubscribed = notificationTokenService.isUserSubscribedToNotificationType(
                    healthUserCi, NotificationType.CLINICAL_HISTORY_ACCESS);

            if (isSubscribed) {
                var tokens = notificationTokenRepository.findByUserId(healthUser.getId());
                if (tokens != null && !tokens.isEmpty()) {
                    String title = "Clinical history accessed";
                    String body = String.format("Your clinical history was accessed by a health worker at %s",
                            clinicName);
                    for (var t : tokens) {
                        pushNotificationService.sendPushNotificationToToken(title, body, t.getToken());
                    }
                }
            }
        } catch (Exception ignored) {
        }

        ClinicalHistoryDTO dto = new ClinicalHistoryDTO();
        dto.setHealthUser(healthUser.toDto());
        dto.setClinicalDocuments(clinicalDocuments);

        return dto;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
