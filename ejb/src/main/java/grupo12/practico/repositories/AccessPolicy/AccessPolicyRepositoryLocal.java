package grupo12.practico.repositories.AccessPolicy;

import java.util.List;

import grupo12.practico.models.ClinicAccessPolicy;
import grupo12.practico.models.HealthWorkerAccessPolicy;
import jakarta.ejb.Local;

@Local
public interface AccessPolicyRepositoryLocal {
    ClinicAccessPolicy createClinicAccessPolicy(ClinicAccessPolicy clinicAccessPolicy);

    HealthWorkerAccessPolicy createHealthWorkerAccessPolicy(HealthWorkerAccessPolicy healthWorkerAccessPolicy);

    List<ClinicAccessPolicy> findAllClinicAccessPolicies(String healthUserId);
    List<HealthWorkerAccessPolicy> findAllHealthWorkerAccessPolicies(String healthUserId);

    void deleteClinicAccessPolicy(String clinicAccessPolicyId);
    void deleteHealthWorkerAccessPolicy(String healthWorkerAccessPolicyId);
}
