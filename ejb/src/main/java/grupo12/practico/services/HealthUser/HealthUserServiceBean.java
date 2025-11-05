package grupo12.practico.services.HealthUser;

import java.util.List;
import java.util.stream.Collectors;
import grupo12.practico.dtos.HealthUser.AddHealthUserDTO;
import grupo12.practico.dtos.HealthUser.ClinicalDocumentDTO;
import grupo12.practico.dtos.HealthUser.ClinicalHistoryDTO;
import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.models.HealthUser;
import grupo12.practico.repositories.AccessPolicy.AccessPolicyRepositoryLocal;
import grupo12.practico.repositories.HealthUser.HealthUserRepositoryLocal;
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

    @EJB
    private HealthUserRepositoryLocal healthUserRepository;

    @EJB
    private AccessPolicyRepositoryLocal accessPolicyRepository;

    @Override
    public List<HealthUserDTO> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize) {
        return healthUserRepository.findAll(clinicName, name, ci, pageIndex, pageSize).stream()
                .map(HealthUser::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public HealthUserDTO create(AddHealthUserDTO addHealthUserDTO) {
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

        return healthUserRepository.create(healthUser).toDto();
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
                        .anyMatch(policy -> policy.getHealthWorkerCi().equals(healthWorkerCi) && policy.getClinicName().equals(clinicName));
        
        if (!hasClinicPolicy && !hasHealthWorkerPolicy) {
            throw new ValidationException("Access denied to clinical history");
        }

        List<ClinicalDocumentDTO> clinicalDocuments = healthUserRepository.findClinicalHistory(healthUserCi);
        
        ClinicalHistoryDTO dto = new ClinicalHistoryDTO();
        dto.setHealthUser(healthUser.toDto());
        dto.setClinicalDocuments(clinicalDocuments);
        
        return dto;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
