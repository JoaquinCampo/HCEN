package grupo12.practico.repositories.AccessRequest;

import java.util.Optional;

import grupo12.practico.models.AccessRequest;
import jakarta.ejb.Local;

@Local
public interface AccessRequestRepositoryLocal {
    AccessRequest add(AccessRequest accessRequest);

    AccessRequest findById(String id);

    Optional<AccessRequest> findExisting(String healthUserId, String healthWorkerId, String clinicId,
            String specialtyId);

    void delete(AccessRequest accessRequest);
}
