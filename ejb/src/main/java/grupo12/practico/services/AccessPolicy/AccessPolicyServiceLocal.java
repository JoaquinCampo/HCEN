package grupo12.practico.services.AccessPolicy;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddSpecialtyAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.ClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.SpecialtyAccessPolicyDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AccessPolicyServiceLocal {
    ClinicAccessPolicyDTO createClinicAccessPolicy(AddClinicAccessPolicyDTO dto);
    HealthWorkerAccessPolicyDTO createHealthWorkerAccessPolicy(AddHealthWorkerAccessPolicyDTO dto);
    SpecialtyAccessPolicyDTO createSpecialtyAccessPolicy(AddSpecialtyAccessPolicyDTO dto);

    List<ClinicAccessPolicyDTO> findAllClinicAccessPolicies(String healthUserCi);
    List<HealthWorkerAccessPolicyDTO> findAllHealthWorkerAccessPolicies(String healthUserCi);
    List<SpecialtyAccessPolicyDTO> findAllSpecialtyAccessPolicies(String healthUserCi);

    void deleteClinicAccessPolicy(String clinicAccessPolicyId);
    void deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId);
    void deleteSpecialtyAccessPolicy(String specialtyAccessPolicyId);

    boolean hasClinicAccess(String healthUserCi, String clinicName);
    boolean hasHealthWorkerAccess(String healthUserCi, String healthWorkerCi);
    boolean hasSpecialtyAccess(String healthUserCi, List<String> specialtyNames);
}
