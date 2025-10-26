package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessDTO;
import grupo12.practico.dtos.AccessRequest.GrantAccessResultDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AccessRequestServiceLocal {
    AccessRequestDTO create(AddAccessRequestDTO dto);

    AccessRequestDTO findById(String id);

    List<AccessRequestDTO> findAllByHealthUserId(String healthUserId);

    GrantAccessResultDTO grantAccessByHealthWorker(String accessRequestId, GrantAccessDTO dto);

    GrantAccessResultDTO grantAccessByClinic(String accessRequestId, GrantAccessDTO dto);

    GrantAccessResultDTO grantAccessBySpecialty(String accessRequestId, GrantAccessDTO dto);

    GrantAccessResultDTO denyAccess(String accessRequestId, GrantAccessDTO dto);
}
