package grupo12.practico.repositories.HealthWorkerAccessPolicy;

import java.util.Optional;

import grupo12.practico.models.HealthWorkerAccessPolicy;
import jakarta.ejb.Remote;

@Remote
public interface HealthWorkerAccessPolicyRepositoryRemote {
    HealthWorkerAccessPolicy add(HealthWorkerAccessPolicy policy);

    Optional<HealthWorkerAccessPolicy> findByHealthUserAndHealthWorker(String healthUserId, String healthWorkerId);
}
