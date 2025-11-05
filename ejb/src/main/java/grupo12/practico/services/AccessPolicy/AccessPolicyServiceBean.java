package grupo12.practico.services.AccessPolicy;

import java.util.List;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.ClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthUser;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import grupo12.practico.repositories.AccessPolicy.AccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
import grupo12.practico.services.Clinic.ClinicServiceLocal;
import grupo12.practico.services.HealthWorker.HealthWorkerServiceLocal;
import grupo12.practico.dtos.Clinic.ClinicDTO;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;
import java.util.ArrayList;

@Stateless
@Local(AccessPolicyServiceLocal.class)
@Remote(AccessPolicyServiceRemote.class)
public class AccessPolicyServiceBean implements AccessPolicyServiceRemote {

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private HealthWorkerServiceLocal healthWorkerService;

    @EJB
    private AccessPolicyRepositoryLocal accessPolicyRepository;

    @EJB
    private ClinicServiceLocal clinicService;

    @Override
    public ClinicAccessPolicyDTO createClinicAccessPolicy(AddClinicAccessPolicyDTO dto) {
        if (dto == null) {
            throw new ValidationException("Clinic access policy payload is required");
        }
        if (isBlank(dto.getHealthUserId())) {
            throw new ValidationException("Health user id is required");
        }
        if (isBlank(dto.getClinicName())) {
            throw new ValidationException("Clinic id is required");
        }

        HealthUser healthUser = healthUserRepository.findById(dto.getHealthUserId());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        ClinicAccessPolicy clinicAccessPolicy = new ClinicAccessPolicy();
        clinicAccessPolicy.setHealthUser(healthUser);
        clinicAccessPolicy.setClinicName(dto.getClinicName());

        ClinicAccessPolicy createdClinicAccessPolicy = accessPolicyRepository
                .createClinicAccessPolicy(clinicAccessPolicy);

        ClinicDTO clinicDTO = clinicService.findByName(dto.getClinicName());

        ClinicAccessPolicyDTO result = new ClinicAccessPolicyDTO();
        result.setId(createdClinicAccessPolicy.getId());
        result.setHealthUserId(healthUser.getId());
        result.setClinic(clinicDTO);

        return result;
    }

    @Override
    public HealthWorkerAccessPolicyDTO createHealthWorkerAccessPolicy(AddHealthWorkerAccessPolicyDTO dto) {
        if (dto == null) {
            throw new ValidationException("Health worker access policy payload is required");
        }
        if (isBlank(dto.getHealthUserId())) {
            throw new ValidationException("Health user id is required");
        }
        if (isBlank(dto.getHealthWorkerCi())) {
            throw new ValidationException("Health worker ci is required");
        }

        HealthUser healthUser = healthUserRepository.findById(dto.getHealthUserId());
        if (healthUser == null) {
            throw new ValidationException("Health user not found");
        }

        HealthWorkerAccessPolicy healthWorkerAccessPolicy = new HealthWorkerAccessPolicy();
        healthWorkerAccessPolicy.setHealthUser(healthUser);
        healthWorkerAccessPolicy.setHealthWorkerCi(dto.getHealthWorkerCi());

        HealthWorkerAccessPolicy createdHealthWorkerPolicy = accessPolicyRepository
                .createHealthWorkerAccessPolicy(healthWorkerAccessPolicy);

        HealthWorkerDTO healthWorkerDTO = healthWorkerService.findByClinicAndCi(dto.getClinicName(),
                dto.getHealthWorkerCi());

        HealthWorkerAccessPolicyDTO result = new HealthWorkerAccessPolicyDTO();
        result.setId(createdHealthWorkerPolicy.getId());
        result.setHealthUserId(healthUser.getId());
        result.setHealthWorker(healthWorkerDTO);

        return result;
    }

    @Override
    public List<ClinicAccessPolicyDTO> findAllClinicAccessPolicies(String healthUserId) {
        if (isBlank(healthUserId)) {
            throw new ValidationException("Health user id is required");
        }

        List<ClinicAccessPolicy> clinicAccessPolicies = accessPolicyRepository
                .findAllClinicAccessPolicies(healthUserId);

        List<ClinicAccessPolicyDTO> result = new ArrayList<>();
        for (ClinicAccessPolicy policy : clinicAccessPolicies) {
            ClinicAccessPolicyDTO dto = new ClinicAccessPolicyDTO();
            dto.setId(policy.getId());
            dto.setHealthUserId(policy.getHealthUser().getId());
            dto.setClinic(clinicService.findByName(policy.getClinicName()));
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<HealthWorkerAccessPolicyDTO> findAllHealthWorkerAccessPolicies(String healthUserId) {
        if (isBlank(healthUserId)) {
            throw new ValidationException("Health user id is required");
        }

        List<HealthWorkerAccessPolicy> healthWorkerAccessPolicies = accessPolicyRepository
                .findAllHealthWorkerAccessPolicies(healthUserId);

        List<HealthWorkerAccessPolicyDTO> result = new ArrayList<>();
        for (HealthWorkerAccessPolicy policy : healthWorkerAccessPolicies) {
            HealthWorkerAccessPolicyDTO dto = new HealthWorkerAccessPolicyDTO();
            dto.setId(policy.getId());
            dto.setHealthUserId(healthUserId);
            dto.setHealthWorker(
                    healthWorkerService.findByClinicAndCi(policy.getClinicName(), policy.getHealthWorkerCi()));
            dto.setClinic(clinicService.findByName(policy.getClinicName()));
            result.add(dto);
        }
        return result;
    }

    @Override
    public void deleteClinicAccessPolicy(String clinicAccessPolicyId) {
        if (isBlank(clinicAccessPolicyId)) {
            throw new ValidationException("Clinic access policy id is required");
        }

        accessPolicyRepository.deleteClinicAccessPolicy(clinicAccessPolicyId);
    }

    @Override
    public void deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId) {
        if (isBlank(healthWorkerAccessPolicyId)) {
            throw new ValidationException("Health worker access policy id is required");
        }

        accessPolicyRepository.deleteHealthWorkerAccessPolicy(healthWorkerAccessPolicyId);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
