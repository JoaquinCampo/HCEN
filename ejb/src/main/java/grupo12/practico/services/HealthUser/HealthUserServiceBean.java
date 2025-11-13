package grupo12.practico.services.HealthUser;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import grupo12.practico.dtos.PaginationDTO;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryAccessLogResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryResponseDTO;
import grupo12.practico.dtos.ClinicalHistory.HealthUserAccessHistoryResponseDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.services.AccessPolicy.AccessPolicyServiceLocal;
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
    private AccessPolicyServiceLocal accessPolicyService;

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

    @Override
    public ClinicalHistoryResponseDTO fetchClinicalHistory(String healthUserCi, String healthWorkerCi,
            String clinicName, String providerName) {

        boolean clinicAccess = accessPolicyService.hasClinicAccess(healthUserCi, clinicName);
        boolean workerAccess = accessPolicyService.hasHealthWorkerAccess(healthUserCi, healthWorkerCi);
        if (!clinicAccess && !workerAccess)
            throw new ValidationException("Access denied to clinical history for health user CI: " + healthUserCi);

        HealthUserDTO healthUser = healthUserRepository.findByCi(healthUserCi).toDto();

        List<DocumentResponseDTO> documents = healthUserRepository.fetchClinicalHistory(healthUserCi, healthWorkerCi,
                clinicName, providerName);

        ClinicalHistoryResponseDTO response = new ClinicalHistoryResponseDTO();
        response.setHealthUser(healthUser);
        response.setDocuments(documents);

        return response;
    }

    @Override
    public HealthUserAccessHistoryResponseDTO fetchHealthUserAccessHistory(String healthUserCi) {
        HealthUserDTO healthUser = findByCi(healthUserCi);
        List<ClinicalHistoryAccessLogResponseDTO> accessHistory = healthUserRepository
                .fetchHealthUserAccessHistory(healthUserCi);
        return new HealthUserAccessHistoryResponseDTO(healthUser, accessHistory);
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
