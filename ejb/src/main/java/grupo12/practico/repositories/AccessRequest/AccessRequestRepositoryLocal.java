package grupo12.practico.repositories.AccessRequest;

import java.util.List;
import java.util.Optional;

import grupo12.practico.models.AccessRequest;
import jakarta.ejb.Local;

@Local
public interface AccessRequestRepositoryLocal {
    AccessRequest add(AccessRequest accessRequest);

    AccessRequest findById(String id);

    Optional<AccessRequest> findExisting(String healthUserId, String healthWorkerCi, String clinicName);

    void delete(AccessRequest accessRequest);

    List<AccessRequest> findAllByHealthUserId(String healthUserId);
}
