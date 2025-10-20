package grupo12.practico.repositories.ClinicAccessPolicy;

import java.util.Optional;

import grupo12.practico.models.ClinicAccessPolicy;
import jakarta.ejb.Remote;

@Remote
public interface ClinicAccessPolicyRepositoryRemote {
    ClinicAccessPolicy add(ClinicAccessPolicy policy);

    Optional<ClinicAccessPolicy> findByHealthUserAndClinic(String healthUserId, String clinicId);
}
