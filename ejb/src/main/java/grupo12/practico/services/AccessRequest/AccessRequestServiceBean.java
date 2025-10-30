package grupo12.practico.services.AccessRequest;

import java.util.List;
import java.util.stream.Collectors;
import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessResultDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import grupo12.practico.repositories.ClinicAccessPolicy.ClinicAccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorkerAccessPolicy.HealthWorkerAccessPolicyRepositoryLocal;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.repositories.NotificationToken.NotificationTokenRepositoryLocal;
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
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private HealthWorkerAccessPolicyRepositoryLocal healthWorkerAccessPolicyRepository;

    @EJB
    private ClinicAccessPolicyRepositoryLocal clinicAccessPolicyRepository;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private NotificationTokenRepositoryLocal notificationTokenRepository;

    @EJB
    private PushNotificationServiceLocal pushNotificationService;

    @Override
    public AccessRequestDTO create(AddAccessRequestDTO dto) {
        validatePayload(dto);

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(dto.getHealthUserCi());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        boolean duplicateExists = accessRequestRepository
                .findExisting(healthUser.getId(), dto.getHealthWorkerCi(), dto.getClinicName())
                .isPresent();
        if (duplicateExists) {
            throw new ValidationException("An access request already exists for this combination");
        }

        HealthWorkerDTO healthWorker = healthWorkerService.findByClinicAndCi(dto.getClinicName(),
                dto.getHealthWorkerCi());
        if (healthWorker == null) {
            throw new ValidationException("Health worker not found");
        }

        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setHealthUser(healthUser);
        accessRequest.setHealthWorkerCi(dto.getHealthWorkerCi());
        accessRequest.setClinicName(dto.getClinicName());

        AccessRequest persisted = accessRequestRepository.add(accessRequest);

        try {
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
        } catch (Exception ignored) {
        }

        return persisted.toDto();
    }

    @Override
    public AccessRequestDTO findById(String id) {
        AccessRequest accessRequest = accessRequestRepository.findById(id);
        return accessRequest != null ? accessRequest.toDto() : null;
    }

    @Override
    public GrantAccessResultDTO grantAccessByHealthWorker(String accessRequestId, GrantAccessDTO dto) {
        AccessRequest accessRequest = getAccessRequestOrThrow(accessRequestId);
        validateGrantPayload(accessRequestId, dto);

        HealthWorkerDTO healthWorker = healthWorkerService.findByClinicAndCi(accessRequest.getClinicName(),
                accessRequest.getHealthWorkerCi());
        if (healthWorker == null) {
            throw new ValidationException("Access request does not contain a health worker reference");
        }

        HealthUser healthUser = requireHealthUser(dto.getHealthUserId());

        boolean policyExists = healthWorkerAccessPolicyRepository
                .findByHealthUserAndHealthWorker(healthUser.getId(), healthWorker.getCi())
                .isPresent();
        if (policyExists) {
            throw new ValidationException("Access policy already exists for this health worker and user");
        }

        HealthWorkerAccessPolicy policy = new HealthWorkerAccessPolicy();
        policy.setHealthUser(healthUser);
        policy.setHealthWorkerCi(healthWorker.getCi());

        HealthWorkerAccessPolicy savedPolicy = healthWorkerAccessPolicyRepository.add(policy);
        accessRequestRepository.delete(accessRequest);

        return GrantAccessResultDTO.accepted(savedPolicy.getId(), healthUser.getId(), "HEALTH_WORKER",
                healthWorker.getCi(), savedPolicy.getCreatedAt());
    }

    @Override
    public GrantAccessResultDTO grantAccessByClinic(String accessRequestId, GrantAccessDTO dto) {
        AccessRequest accessRequest = getAccessRequestOrThrow(accessRequestId);
        validateGrantPayload(accessRequestId, dto);

        ClinicDTO clinic = clinicService.findById(accessRequest.getClinicName());
        if (clinic == null) {
            throw new ValidationException("Access request does not contain a clinic reference");
        }

        HealthUser healthUser = requireHealthUser(dto.getHealthUserId());

        boolean policyExists = clinicAccessPolicyRepository
                .findByHealthUserAndClinic(healthUser.getId(), clinic.getId())
                .isPresent();
        if (policyExists) {
            throw new ValidationException("Access policy already exists for this clinic and user");
        }

        ClinicAccessPolicy policy = new ClinicAccessPolicy();
        policy.setHealthUser(healthUser);
        policy.setClinic(clinic);

        ClinicAccessPolicy savedPolicy = clinicAccessPolicyRepository.add(policy);
        accessRequestRepository.delete(accessRequest);

        return GrantAccessResultDTO.accepted(savedPolicy.getId(), healthUser.getId(), "CLINIC",
                clinic.getId(), savedPolicy.getGrantedAt());
    }

    @Override
    public GrantAccessResultDTO denyAccess(String accessRequestId, GrantAccessDTO dto) {
        AccessRequest accessRequest = getAccessRequestOrThrow(accessRequestId);
        validateGrantPayload(accessRequestId, dto);

        accessRequestRepository.delete(accessRequest);
        return GrantAccessResultDTO.denied(dto.getHealthUserId(), "ACCESS_REQUEST", accessRequest.getId());
    }

    @Override
    public List<AccessRequestDTO> findAllByHealthUserId(String healthUserId) {
        List<AccessRequest> accessRequests = accessRequestRepository.findAllByHealthUserId(healthUserId);
        return accessRequests.stream().map(AccessRequest::toDto).collect(Collectors.toList());
    }

    private void validatePayload(AddAccessRequestDTO dto) {
        if (dto == null) {
            throw new ValidationException("Access request payload is required");
        }
        if (isBlank(dto.getHealthUserId())) {
            throw new ValidationException("Health user id is required");
        }
        if (isBlank(dto.getHealthWorkerId())) {
            throw new ValidationException("Health worker id is required");
        }
        if (isBlank(dto.getClinicId())) {
            throw new ValidationException("Clinic id is required");
        }
        if (isBlank(dto.getSpecialtyId())) {
            throw new ValidationException("Specialty id is required");
        }
    }

    private void validateGrantPayload(String pathAccessRequestId, GrantAccessDTO dto) {
        if (dto == null) {
            throw new ValidationException("Grant decision payload is required");
        }
        if (isBlank(dto.getHealthUserId())) {
            throw new ValidationException("Health user id is required");
        }
        if (!isBlank(dto.getAccessRequestId())
                && !dto.getAccessRequestId().equals(pathAccessRequestId)) {
            throw new ValidationException("Access request id mismatch");
        }
    }

    private AccessRequest getAccessRequestOrThrow(String accessRequestId) {
        if (isBlank(accessRequestId)) {
            throw new ValidationException("Access request id is required");
        }
        AccessRequest accessRequest = accessRequestRepository.findById(accessRequestId);
        if (accessRequest == null) {
            throw new ValidationException("Access request not found");
        }
        return accessRequest;
    }

    private HealthUser requireHealthUser(String healthUserId) {
        HealthUser healthUser = healthUserRepository.findById(healthUserId);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }
        return healthUser;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
