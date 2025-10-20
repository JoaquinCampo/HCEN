package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.models.AccessRequestStatus;
import grupo12.practico.models.Clinic;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorker;
import grupo12.practico.models.Specialty;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import grupo12.practico.repositories.Clinic.ClinicRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.repositories.HealthWorker.HealthWorkerRepositoryLocal;
import grupo12.practico.repositories.Specialty.SpecialtyRepositoryLocal;
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
