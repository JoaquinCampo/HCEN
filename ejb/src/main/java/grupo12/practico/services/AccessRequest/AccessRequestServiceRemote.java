package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import jakarta.ejb.Remote;

@Remote
public interface AccessRequestServiceRemote {
    AccessRequestDTO create(AddAccessRequestDTO dto);

    AccessRequestDTO findById(String id);
}
