package grupo12.practico.services.AccessPolicy;

import grupo12.practico.dtos.AccessPolicy.AddClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.AddHealthWorkerAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.ClinicAccessPolicyDTO;
import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AccessPolicyServiceLocal {
    ClinicAccessPolicyDTO createClinicAccessPolicy(AddClinicAccessPolicyDTO dto);

    HealthWorkerAccessPolicyDTO createHealthWorkerAccessPolicy(AddHealthWorkerAccessPolicyDTO dto);

    List<ClinicAccessPolicyDTO> findAllClinicAccessPolicies(String healthUserId);
    List<HealthWorkerAccessPolicyDTO> findAllHealthWorkerAccessPolicies(String healthUserId);

    void deleteClinicAccessPolicy(String clinicAccessPolicyId);
    void deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId);
}
