package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessDecisionDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessResultDTO;
import jakarta.ejb.Remote;

@Remote
public interface AccessRequestServiceRemote {
    AccessRequestDTO create(AddAccessRequestDTO dto);

    AccessRequestDTO findById(String id);

    GrantAccessResultDTO grantAccessByHealthWorker(String accessRequestId, GrantAccessDecisionDTO dto);

    GrantAccessResultDTO grantAccessByClinic(String accessRequestId, GrantAccessDecisionDTO dto);

    GrantAccessResultDTO grantAccessBySpecialty(String accessRequestId, GrantAccessDecisionDTO dto);
}
