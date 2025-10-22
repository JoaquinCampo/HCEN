package grupo12.practico.services.AccessRequest;

import java.util.List;
import java.util.stream.Collectors;
import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessDecisionDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessResultDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.AccessRequestStatus;
import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import grupo12.practico.models.Specialty;
import grupo12.practico.models.SpecialtyAccessPolicy;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.ClinicAccessPolicy.ClinicAccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.HealthWorkerAccessPolicy.HealthWorkerAccessPolicyRepositoryLocal;
import grupo12.practico.repositories.Specialty.SpecialtyRepositoryLocal;
import grupo12.practico.repositories.SpecialtyAccessPolicy.SpecialtyAccessPolicyRepositoryLocal;
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
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @EJB
    private ClinicRepositoryLocal clinicRepository;

    @EJB
    private SpecialtyRepositoryLocal specialtyRepository;

    @EJB
    private HealthWorkerAccessPolicyRepositoryLocal healthWorkerAccessPolicyRepository;

    @EJB
    private ClinicAccessPolicyRepositoryLocal clinicAccessPolicyRepository;

    @EJB
    private SpecialtyAccessPolicyRepositoryLocal specialtyAccessPolicyRepository;

    @Override
    public AccessRequestDTO create(AddAccessRequestDTO dto) {
        validatePayload(dto);

        HealthUser healthUser = healthUserRepository.findById(dto.getHealthUserId());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        HealthWorker healthWorker = healthWorkerRepository.findById(dto.getHealthWorkerId());
        if (healthWorker == null) {
            throw new ValidationException("Health worker not found");
        }

        Clinic clinic = clinicRepository.findById(dto.getClinicId());
        if (clinic == null) {
            throw new ValidationException("Clinic not found");
        }

        Specialty specialty = specialtyRepository.findById(dto.getSpecialtyId());
        if (specialty == null) {
            throw new ValidationException("Specialty not found");
        }

        boolean isAssignedToClinic = healthWorker.getClinics() != null
                && healthWorker.getClinics().contains(clinic);
        if (!isAssignedToClinic) {
            throw new ValidationException("Health worker is not associated with the provided clinic");
        }

        boolean duplicateExists = accessRequestRepository
                .findExisting(healthUser.getId(), healthWorker.getId(), clinic.getId(), specialty.getId())
                .isPresent();
        if (duplicateExists) {
            throw new ValidationException("An access request already exists for this combination");
        }

        AccessRequest accessRequest = new AccessRequest();
        accessRequest.setHealthUser(healthUser);
        accessRequest.setHealthWorker(healthWorker);
        accessRequest.setClinic(clinic);
        accessRequest.setSpecialty(specialty);
        accessRequest.setStatus(AccessRequestStatus.PENDING);

        AccessRequest persisted = accessRequestRepository.add(accessRequest);
        return persisted.toDto();
    }

    @Override
    public AccessRequestDTO findById(String id) {
        AccessRequest accessRequest = accessRequestRepository.findById(id);
        return accessRequest != null ? accessRequest.toDto() : null;
    }

    @Override
    public GrantAccessResultDTO grantAccessByHealthWorker(String accessRequestId, GrantAccessDecisionDTO dto) {
        AccessRequest accessRequest = getAccessRequestOrThrow(accessRequestId);
        validateGrantPayload(accessRequestId, dto);

        HealthWorker healthWorker = accessRequest.getHealthWorker();
        if (healthWorker == null) {
            throw new ValidationException("Access request does not contain a health worker reference");
        }

        String targetId = healthWorker.getId();

        if (!dto.isAccepted()) {
            accessRequestRepository.delete(accessRequest);
            return GrantAccessResultDTO.denied(dto.getHealthUserId(), "HEALTH_WORKER", targetId);
        }

        HealthUser healthUser = requireHealthUser(dto.getHealthUserId());

        boolean policyExists = healthWorkerAccessPolicyRepository
                .findByHealthUserAndHealthWorker(healthUser.getId(), healthWorker.getId())
                .isPresent();
        if (policyExists) {
            throw new ValidationException("Access policy already exists for this health worker and user");
        }

        HealthWorkerAccessPolicy policy = new HealthWorkerAccessPolicy();
        policy.setHealthUser(healthUser);
        policy.setHealthWorker(healthWorker);

        HealthWorkerAccessPolicy savedPolicy = healthWorkerAccessPolicyRepository.add(policy);
        accessRequestRepository.delete(accessRequest);

        return GrantAccessResultDTO.accepted(savedPolicy.getId(), healthUser.getId(), "HEALTH_WORKER",
                healthWorker.getId(), savedPolicy.getGrantedAt());
    }

    @Override
    public GrantAccessResultDTO grantAccessByClinic(String accessRequestId, GrantAccessDecisionDTO dto) {
        AccessRequest accessRequest = getAccessRequestOrThrow(accessRequestId);
        validateGrantPayload(accessRequestId, dto);

        Clinic clinic = accessRequest.getClinic();
        if (clinic == null) {
            throw new ValidationException("Access request does not contain a clinic reference");
        }

        String targetId = clinic.getId();

        if (!dto.isAccepted()) {
            accessRequestRepository.delete(accessRequest);
            return GrantAccessResultDTO.denied(dto.getHealthUserId(), "CLINIC", targetId);
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
    public GrantAccessResultDTO grantAccessBySpecialty(String accessRequestId, GrantAccessDecisionDTO dto) {
        AccessRequest accessRequest = getAccessRequestOrThrow(accessRequestId);
        validateGrantPayload(accessRequestId, dto);

        Specialty specialty = accessRequest.getSpecialty();
        if (specialty == null) {
            throw new ValidationException("Access request does not contain a specialty reference");
        }

        String targetId = specialty.getId();

        if (!dto.isAccepted()) {
            accessRequestRepository.delete(accessRequest);
            return GrantAccessResultDTO.denied(dto.getHealthUserId(), "SPECIALTY", targetId);
        }

        HealthUser healthUser = requireHealthUser(dto.getHealthUserId());

        boolean policyExists = specialtyAccessPolicyRepository
                .findByHealthUserAndSpecialty(healthUser.getId(), specialty.getId())
                .isPresent();
        if (policyExists) {
            throw new ValidationException("Access policy already exists for this specialty and user");
        }

        SpecialtyAccessPolicy policy = new SpecialtyAccessPolicy();
        policy.setHealthUser(healthUser);
        policy.setSpecialty(specialty);

        SpecialtyAccessPolicy savedPolicy = specialtyAccessPolicyRepository.add(policy);
        accessRequestRepository.delete(accessRequest);

        return GrantAccessResultDTO.accepted(savedPolicy.getId(), healthUser.getId(), "SPECIALTY",
                specialty.getId(), savedPolicy.getGrantedAt());
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

    private void validateGrantPayload(String pathAccessRequestId, GrantAccessDecisionDTO dto) {
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
