package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AccessRequestServiceLocal {
    AccessRequestDTO createAccessRequest(AddAccessRequestDTO dto);

    AccessRequestDTO findAccessRequestById(String id);

    List<AccessRequestDTO> findAllAccessRequests(String healthUserCi, String healthWorkerCi, String clinicName);

    void deleteAccessRequest(String accessRequestId);
    
    void deleteAccessRequest(String accessRequestId, boolean logAsDenied);
}
