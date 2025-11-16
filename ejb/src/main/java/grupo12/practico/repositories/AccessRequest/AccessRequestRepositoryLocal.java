package grupo12.practico.repositories.AccessRequest;

import java.util.List;

import grupo12.practico.models.AccessRequest;
import jakarta.ejb.Local;

@Local
public interface AccessRequestRepositoryLocal {
    AccessRequest createAccessRequest(AccessRequest accessRequest);

    AccessRequest findAccessRequestById(String id);

    List<AccessRequest> findAllAccessRequests(String healthUserId, String healthWorkerCi, String clinicName);

    void deleteAccessRequest(String accessRequestId);
}
