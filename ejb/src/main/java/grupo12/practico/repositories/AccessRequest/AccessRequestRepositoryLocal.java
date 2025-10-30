package grupo12.practico.repositories.AccessRequest;

import java.util.List;

import grupo12.practico.models.AccessRequest;
import jakarta.ejb.Local;

@Local
public interface AccessRequestRepositoryLocal {
    AccessRequest create(AccessRequest accessRequest);

    AccessRequest findById(String id);

    List<AccessRequest> findAll(String healthUserId, String healthWorkerCi, String clinicName);

    void delete(String accessRequestId);
}
