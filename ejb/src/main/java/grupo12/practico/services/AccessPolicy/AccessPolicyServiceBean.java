package grupo12.practico.services.AccessPolicy;

import java.util.List;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.ClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.SpecialtyAccessPolicyDTO;
import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import grupo12.practico.models.SpecialtyAccessPolicy;
import grupo12.practico.repositories.AccessPolicy.AccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.services.AccessRequest.AccessRequestServiceLocal;
import grupo12.practico.services.Logger.LoggerServiceLocal;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.models.AccessRequest;
import grupo12.practico.repositories.AccessRequest.AccessRequestRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Local(AccessPolicyServiceLocal.class)
@Remote(AccessPolicyServiceRemote.class)
public class AccessPolicyServiceBean implements AccessPolicyServiceRemote {

    private static final Logger LOGGER = Logger.getLogger(AccessPolicyServiceBean.class.getName());

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private AccessPolicyRepositoryLocal accessPolicyRepository;

    @EJB
    private ClinicServiceLocal clinicService;

    @EJB
    private AccessRequestServiceLocal accessRequestService;

    @EJB
    private LoggerServiceLocal loggerService;

    @EJB
    private AccessRequestRepositoryLocal accessRequestRepository;

    @Override
    public ClinicAccessPolicyDTO createClinicAccessPolicy(AddClinicAccessPolicyDTO dto) {
        if (dto == null) {
            throw new ValidationException("Clinic access policy payload is required");
        }
        if (dto.getHealthUserCi() == null || dto.getHealthUserCi().isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (dto.getClinicName() == null || dto.getClinicName().isBlank()) {
            throw new ValidationException("Clinic name is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(dto.getHealthUserCi());

        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        ClinicAccessPolicy clinicAccessPolicy = new ClinicAccessPolicy();
        clinicAccessPolicy.setHealthUser(healthUser);
        clinicAccessPolicy.setClinicName(dto.getClinicName());

        ClinicAccessPolicy createdClinicAccessPolicy = accessPolicyRepository
                .createClinicAccessPolicy(clinicAccessPolicy);

        ClinicDTO clinicDTO = clinicService.findClinicByName(dto.getClinicName());

        ClinicAccessPolicyDTO result = new ClinicAccessPolicyDTO();
        result.setId(createdClinicAccessPolicy.getId());
        result.setHealthUserCi(healthUser.getCi());
        result.setClinic(clinicDTO);

        // Log access request acceptance
        if (dto.getAccessRequestId() != null && !dto.getAccessRequestId().isBlank()) {
            try {
                AccessRequest accessRequest = accessRequestRepository.findAccessRequestById(dto.getAccessRequestId());
                if (accessRequest != null) {
                    loggerService.logAccessRequestAcceptedByClinic(
                            dto.getAccessRequestId(),
                            healthUser.getCi(),
                            accessRequest.getHealthWorkerCi(),
                            dto.getClinicName(),
                            accessRequest.getSpecialtyNames());
                }

                accessRequestService.deleteAccessRequest(dto.getAccessRequestId());
                LOGGER.info(
                        "Deleted access request " + dto.getAccessRequestId() + " after creating clinic access policy");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Failed to delete access request after creating clinic policy: " + e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public HealthWorkerAccessPolicyDTO createHealthWorkerAccessPolicy(AddHealthWorkerAccessPolicyDTO dto) {
        if (dto == null) {
            throw new ValidationException("Health worker access policy payload is required");
        }
        if (dto.getHealthUserCi() == null || dto.getHealthUserCi().isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (dto.getHealthWorkerCi() == null || dto.getHealthWorkerCi().isBlank()) {
            throw new ValidationException("Health worker CI is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(dto.getHealthUserCi());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        HealthWorkerAccessPolicy healthWorkerAccessPolicy = new HealthWorkerAccessPolicy();
        healthWorkerAccessPolicy.setHealthUser(healthUser);
        healthWorkerAccessPolicy.setHealthWorkerCi(dto.getHealthWorkerCi());
        healthWorkerAccessPolicy.setClinicName(dto.getClinicName());

        HealthWorkerAccessPolicy createdHealthWorkerPolicy = accessPolicyRepository
                .createHealthWorkerAccessPolicy(healthWorkerAccessPolicy);

        HealthWorkerDTO healthWorkerDTO = healthWorkerService.findByClinicAndCi(dto.getClinicName(),
                dto.getHealthWorkerCi());

        HealthWorkerAccessPolicyDTO result = new HealthWorkerAccessPolicyDTO();
        result.setId(createdHealthWorkerPolicy.getId());
        result.setHealthUserCi(healthUser.getCi());
        result.setHealthWorker(healthWorkerDTO);
        result.setClinic(clinicService.findClinicByName(dto.getClinicName()));

        // Log access request acceptance
        if (dto.getAccessRequestId() != null && !dto.getAccessRequestId().isBlank()) {
            try {
                AccessRequest accessRequest = accessRequestRepository.findAccessRequestById(dto.getAccessRequestId());
                if (accessRequest != null) {
                    loggerService.logAccessRequestAcceptedByHealthWorker(
                            dto.getAccessRequestId(),
                            healthUser.getCi(),
                            dto.getHealthWorkerCi(),
                            dto.getClinicName(),
                            accessRequest.getSpecialtyNames());
                }

                accessRequestService.deleteAccessRequest(dto.getAccessRequestId());
                LOGGER.info("Deleted access request " + dto.getAccessRequestId()
                        + " after creating health worker access policy");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Failed to delete access request after creating health worker policy: " + e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public SpecialtyAccessPolicyDTO createSpecialtyAccessPolicy(AddSpecialtyAccessPolicyDTO dto) {
        if (dto == null) {
            throw new ValidationException("Specialty access policy payload is required");
        }
        if (dto.getHealthUserCi() == null || dto.getHealthUserCi().isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (dto.getSpecialtyName() == null || dto.getSpecialtyName().isBlank()) {
            throw new ValidationException("Specialty name is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(dto.getHealthUserCi());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        SpecialtyAccessPolicy specialtyAccessPolicy = new SpecialtyAccessPolicy();
        specialtyAccessPolicy.setHealthUser(healthUser);
        specialtyAccessPolicy.setSpecialtyName(dto.getSpecialtyName());

        SpecialtyAccessPolicy createdSpecialtyPolicy = accessPolicyRepository
                .createSpecialtyAccessPolicy(specialtyAccessPolicy);

        SpecialtyAccessPolicyDTO result = new SpecialtyAccessPolicyDTO();
        result.setId(createdSpecialtyPolicy.getId());
        result.setHealthUserCi(healthUser.getCi());
        result.setSpecialtyName(dto.getSpecialtyName());

        // Log access request acceptance if applicable
        if (dto.getAccessRequestId() != null && !dto.getAccessRequestId().isBlank()) {
            try {
                AccessRequest accessRequest = accessRequestRepository.findAccessRequestById(dto.getAccessRequestId());
                if (accessRequest != null) {
                    loggerService.logAccessRequestAcceptedBySpecialty(
                            dto.getAccessRequestId(),
                            healthUser.getCi(),
                            accessRequest.getHealthWorkerCi(),
                            accessRequest.getClinicName(),
                            accessRequest.getSpecialtyNames());
                }

                accessRequestService.deleteAccessRequest(dto.getAccessRequestId());
                LOGGER.info("Deleted access request " + dto.getAccessRequestId()
                        + " after creating specialty access policy");
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Failed to delete access request after creating specialty policy: " + e.getMessage(), e);
            }
        }

        return result;
    }

    @Override
    public List<ClinicAccessPolicyDTO> findAllClinicAccessPolicies(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user id is required");
        }
        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }
        String healthUserId = healthUser.getId();
        List<ClinicAccessPolicy> clinicAccessPolicies = accessPolicyRepository
                .findAllClinicAccessPolicies(healthUserId);

        List<ClinicAccessPolicyDTO> result = new ArrayList<>();
        for (ClinicAccessPolicy policy : clinicAccessPolicies) {
            ClinicAccessPolicyDTO dto = new ClinicAccessPolicyDTO();
            dto.setId(policy.getId());
            dto.setHealthUserCi(policy.getHealthUser().getCi());
            dto.setClinic(clinicService.findClinicByName(policy.getClinicName()));
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<HealthWorkerAccessPolicyDTO> findAllHealthWorkerAccessPolicies(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user id is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }
        String healthUserId = healthUser.getId();

        List<HealthWorkerAccessPolicy> healthWorkerAccessPolicies = accessPolicyRepository
                .findAllHealthWorkerAccessPolicies(healthUserId);

        List<HealthWorkerAccessPolicyDTO> result = new ArrayList<>();
        for (HealthWorkerAccessPolicy policy : healthWorkerAccessPolicies) {
            HealthWorkerAccessPolicyDTO dto = new HealthWorkerAccessPolicyDTO();
            dto.setId(policy.getId());
            dto.setHealthUserCi(healthUserCi);
            dto.setHealthWorker(
                    healthWorkerService.findByClinicAndCi(policy.getClinicName(), policy.getHealthWorkerCi()));
            dto.setClinic(clinicService.findClinicByName(policy.getClinicName()));
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<SpecialtyAccessPolicyDTO> findAllSpecialtyAccessPolicies(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user id is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }
        String healthUserId = healthUser.getId();

        List<SpecialtyAccessPolicy> specialtyAccessPolicies = accessPolicyRepository
                .findAllSpecialtyAccessPolicies(healthUserId);

        List<SpecialtyAccessPolicyDTO> result = new ArrayList<>();
        for (SpecialtyAccessPolicy policy : specialtyAccessPolicies) {
            SpecialtyAccessPolicyDTO dto = new SpecialtyAccessPolicyDTO();
            dto.setId(policy.getId());
            dto.setHealthUserCi(healthUserCi);
            dto.setSpecialtyName(policy.getSpecialtyName());
            result.add(dto);
        }
        return result;
    }

    @Override
    public void deleteClinicAccessPolicy(String clinicAccessPolicyId) {
        if (clinicAccessPolicyId == null || clinicAccessPolicyId.isBlank()) {
            throw new ValidationException("Clinic access policy id is required");
        }

        accessPolicyRepository.deleteClinicAccessPolicy(clinicAccessPolicyId);
    }

    @Override
    public void deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId) {
        if (healthWorkerAccessPolicyId == null || healthWorkerAccessPolicyId.isBlank()) {
            throw new ValidationException("Health worker access policy id is required");
        }

        accessPolicyRepository.deleteHealthWorkerAccessPolicy(healthWorkerAccessPolicyId);
    }

    @Override
    public void deleteSpecialtyAccessPolicy(String specialtyAccessPolicyId) {
        if (specialtyAccessPolicyId == null || specialtyAccessPolicyId.isBlank()) {
            throw new ValidationException("Specialty access policy id is required");
        }

        accessPolicyRepository.deleteSpecialtyAccessPolicy(specialtyAccessPolicyId);
    }

    @Override
    public boolean hasClinicAccess(String healthUserCi, String clinicName) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (clinicName == null || clinicName.isBlank()) {
            throw new ValidationException("Clinic name is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            return false;
        }

        return accessPolicyRepository.hasClinicAccess(healthUser.getId(), clinicName);
    }

    @Override
    public boolean hasHealthWorkerAccess(String healthUserCi, String healthWorkerCi) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user CI is required");
        }
        if (healthWorkerCi == null || healthWorkerCi.isBlank()) {
            throw new ValidationException("Health worker CI is required");
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            return false;
        }

        return accessPolicyRepository.hasHealthWorkerAccess(healthUser.getId(), healthWorkerCi);
    }

    @Override
    public boolean hasSpecialtyAccess(String healthUserCi, List<String> specialtyNames) {
        if (healthUserCi == null || healthUserCi.isBlank()) {
            throw new ValidationException("Health user CI is required");
        }

        if (specialtyNames == null || specialtyNames.isEmpty()) {
            return false;
        }

        HealthUser healthUser = healthUserRepository.findHealthUserByCi(healthUserCi);
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        return accessPolicyRepository.hasSpecialtyAccess(healthUser.getId(), specialtyNames);
    }
}
