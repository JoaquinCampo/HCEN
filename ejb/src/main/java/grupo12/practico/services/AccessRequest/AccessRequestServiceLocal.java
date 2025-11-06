package grupo12.practico.services.AccessRequest;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import grupo12.practico.dtos.AccessRequest.AddAccessRequestDTO;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface AccessRequestServiceLocal {
    AccessRequestDTO create(AddAccessRequestDTO dto);

    AccessRequestDTO findById(String id);

    List<AccessRequestDTO> findAll(String healthUserCi, String healthWorkerCi, String clinicName);

    void delete(String accessRequestId);
}
