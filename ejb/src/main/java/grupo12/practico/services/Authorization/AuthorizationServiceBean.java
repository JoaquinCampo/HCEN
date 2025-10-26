package grupo12.practico.services.Authorization;

import grupo12.practico.dtos.Authorization.AuthorizationCheckRequestDTO;
import grupo12.practico.dtos.Authorization.AuthorizationDecisionDTO;
import grupo12.practico.dtos.Authorization.AuthorizationDecisionSource;
import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.models.Specialty;
import grupo12.practico.repositories.ClinicAccessPolicy.ClinicAccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.HealthWorkerAccessPolicy.HealthWorkerAccessPolicyRepositoryLocal;
import grupo12.practico.repositories.SpecialtyAccessPolicy.SpecialtyAccessPolicyRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

@Stateless
@Local(AuthorizationServiceLocal.class)
@Remote(AuthorizationServiceRemote.class)
public class AuthorizationServiceBean implements AuthorizationServiceRemote {

    @EJB
    private ClinicAccessPolicyRepositoryLocal clinicAccessPolicyRepository;

    @EJB
    private SpecialtyAccessPolicyRepositoryLocal specialtyAccessPolicyRepository;

    @EJB
    private HealthWorkerAccessPolicyRepositoryLocal healthWorkerAccessPolicyRepository;

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private HealthWorkerRepositoryLocal healthWorkerRepository;

    @Override
    public AuthorizationDecisionDTO checkAccess(AuthorizationCheckRequestDTO request) {
        validateRequest(request);

        HealthUser healthUser = requireHealthUser(request.getHealthUserId());

        boolean clinicAllowed = clinicAccessPolicyRepository
                .findByHealthUserAndClinic(healthUser.getId(), request.getClinicId())
                .isPresent();
        if (clinicAllowed) {
            return AuthorizationDecisionDTO.allowed(AuthorizationDecisionSource.CLINIC_POLICY,
                    "Clinic has access to health user history");
        }

        HealthWorker healthWorker = requireHealthWorker(request.getHealthWorkerId());

        boolean workerAssignedToClinic = healthWorker.getClinics() != null
                && healthWorker.getClinics().stream().map(Clinic::getId).anyMatch(request.getClinicId()::equals);
        if (!workerAssignedToClinic) {
            throw new ValidationException("Health worker is not associated with the provided clinic");
        }

        if (healthWorker.getSpecialties() != null) {
            for (Specialty specialty : healthWorker.getSpecialties()) {
                boolean specialtyAllowed = specialtyAccessPolicyRepository
                        .findByHealthUserAndSpecialty(healthUser.getId(), specialty.getId())
                        .isPresent();
                if (specialtyAllowed) {
                    return AuthorizationDecisionDTO.allowed(AuthorizationDecisionSource.SPECIALTY_POLICY,
                            "Health worker specialty is allowed for this user");
                }
            }
        }

        boolean workerAllowed = healthWorkerAccessPolicyRepository
                .findByHealthUserAndHealthWorker(healthUser.getId(), healthWorker.getId())
                .isPresent();
        if (workerAllowed) {
            return AuthorizationDecisionDTO.allowed(AuthorizationDecisionSource.HEALTH_WORKER_POLICY,
                    "Health worker is explicitly allowed");
        }

        return AuthorizationDecisionDTO.denied("No matching access policy found");
    }

    private void validateRequest(AuthorizationCheckRequestDTO request) {
        if (request == null) {
            throw new ValidationException("Authorization request payload is required");
        }
        if (isBlank(request.getHealthUserId())) {
            throw new ValidationException("Health user id is required");
        }
        if (isBlank(request.getHealthWorkerId())) {
            throw new ValidationException("Health worker id is required");
        }
        if (isBlank(request.getClinicId())) {
            throw new ValidationException("Clinic id is required");
        }
    }

    private HealthUser requireHealthUser(String id) {
        HealthUser healthUser = healthUserRepository.findById(id);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }
        return healthUser;
    }

    private HealthWorker requireHealthWorker(String id) {
        HealthWorker healthWorker = healthWorkerRepository.findById(id);
        if (healthWorker == null) {
            throw new ValidationException("Health worker not found");
        }
        return healthWorker;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
