package grupo12.practico.services.AccessRequest;

import java.util.List;
import java.util.stream.Collectors;
import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.NotificationType;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.HealthUser.HealthUserServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
import grupo12.practico.services.NotificationToken.NotificationTokenServiceLocal;
import grupo12.practico.services.PushNotificationSender.PushNotificationServiceLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

@Stateless
@Local(AccessRequestServiceLocal.class)
@Remote(AccessRequestServiceRemote.class)
public class AccessRequestServiceBean implements AccessRequestServiceRemote {

    @EJB
    private AccessRequestRepositoryLocal accessRequestRepository;

    @EJB
    private ClinicServiceLocal clinicServiceLocal;

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private HealthUserServiceLocal healthUserService;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @EJB
    private NotificationTokenServiceLocal notificationTokenService;

    @EJB
    private PushNotificationServiceLocal pushNotificationService;

    @Override
    public AccessRequestDTO create(AddAccessRequestDTO dto) {
        validatePayload(dto);

        HealthUser healthUser = healthUserRepository.findByCi(dto.getHealthUserCi());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        boolean duplicateExists = accessRequestRepository
                .findAll(healthUser.getId(), dto.getHealthWorkerCi(), dto.getClinicName())
                .size() > 0;
        if (duplicateExists) {
            throw new ValidationException("An access request already exists for this combination");
        }

        HealthWorkerDTO healthWorker = healthWorkerService.findByClinicAndCi(dto.getClinicName(),
                dto.getHealthWorkerCi());
        if (healthWorker == null) {
            throw new ValidationException("Health worker not found");
        }

        ClinicDTO clinic = clinicServiceLocal.findByName(dto.getClinicName());
        if (clinic == null) {
            throw new ValidationException("Clinic not found");
        }

        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setHealthUser(healthUser);
        accessRequest.setHealthWorkerCi(healthWorker.getCi());
        accessRequest.setClinicName(clinic.getName());

        AccessRequest persisted = accessRequestRepository.create(accessRequest);

        try {
            // Check if user is subscribed to ACCESS_REQUEST notifications
            boolean isSubscribed = notificationTokenService.isUserSubscribedToNotificationType(
                    healthUser.getCi(), NotificationType.ACCESS_REQUEST);

            if (isSubscribed) {
                var tokens = notificationTokenRepository.findByUserId(healthUser.getId());
                if (tokens != null && !tokens.isEmpty()) {
                    String title = "New access request";
                    String clinicName = dto.getClinicName() != null ? dto.getClinicName() : "";
                    String healthWorkerName = healthWorker.getFirstName() + " " + healthWorker.getLastName();
                    String body = String.format("%s requested access to your records at %s",
                            healthWorkerName, clinicName);
                    for (var t : tokens) {
                        pushNotificationService.sendPushNotificationToToken(title, body, t.getToken());
                    }
                }
            }
        } catch (Exception ignored) {
        }

        AccessRequestDTO result = new AccessRequestDTO();
        result.setId(persisted.getId());
        result.setHealthUserId(healthUser.getId());
        result.setHealthWorker(healthWorker);
        result.setClinic(clinic);

        return result;
    }

    @Override
    public AccessRequestDTO findById(String id) {
        if (isBlank(id)) {
            throw new ValidationException("Access request id is required");
        }

        AccessRequest accessRequest = accessRequestRepository.findById(id);
        HealthWorkerDTO healthWorkerDTO = healthWorkerService.findByClinicAndCi(
                accessRequest.getClinicName(), accessRequest.getHealthWorkerCi());
        ClinicDTO clinicDTO = clinicServiceLocal.findByName(accessRequest.getClinicName());

        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setId(accessRequest.getId());
        dto.setHealthUserId(accessRequest.getHealthUser().getId());
        dto.setHealthUserCi(accessRequest.getHealthUser().getCi());
        dto.setHealthWorker(healthWorkerDTO);
        dto.setClinic(clinicDTO);

        return accessRequest != null ? dto : null;
    }

    @Override
    public void delete(String accessRequestId) {
        if (isBlank(accessRequestId)) {
            throw new ValidationException("Access request id is required");
        }

        AccessRequest accessRequest = accessRequestRepository.findById(accessRequestId);

        if (accessRequest == null) {
            throw new ValidationException("Access request not found");
        }

        accessRequestRepository.delete(accessRequest.getId());
    }

    @Override
    public List<AccessRequestDTO> findAll(String healthUserCi, String healthWorkerCi, String clinicName) {
        HealthUserDTO healthUser = null;
        if (healthUserCi != null && !healthUserCi.trim().isEmpty()) {
            healthUser = healthUserService.findByCi(healthUserCi);
            if (healthUser == null) {
                throw new ValidationException("Health user not found with CI: " + healthUserCi);
            }
        }

        String healthUserId = healthUser != null ? healthUser.getId() : null;
        List<AccessRequest> accessRequests = accessRequestRepository.findAll(healthUserId, healthWorkerCi, clinicName);

        return accessRequests.stream().map(accessRequest -> {
            HealthWorkerDTO healthWorkerDTO = healthWorkerService.findByClinicAndCi(
                    accessRequest.getClinicName(), accessRequest.getHealthWorkerCi());
            ClinicDTO clinicDTO = clinicServiceLocal.findByName(accessRequest.getClinicName());

            AccessRequestDTO dto = new AccessRequestDTO();
            dto.setId(accessRequest.getId());
            dto.setHealthUserId(accessRequest.getHealthUser().getId());
            dto.setHealthUserCi(accessRequest.getHealthUser().getCi());
            dto.setHealthWorker(healthWorkerDTO);
            dto.setClinic(clinicDTO);

            return dto;
        }).collect(Collectors.toList());
    }

    private void validatePayload(AddAccessRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Access request payload is required");
        }
        if (isBlank(dto.getHealthUserCi())) {
            throw new ValidationException("Health user id is required");
        }
        if (isBlank(dto.getHealthWorkerCi())) {
            throw new ValidationException("Health worker id is required");
        }
        if (isBlank(dto.getClinicName())) {
            throw new ValidationException("Clinic id is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
