package grupo12.practico.services.HealthUser;

import grupo12.practico.dtos.ClinicalHistory.HealthUserAccessHistoryResponseDTO;
import jakarta.ejb.Remote;

@Remote
public interface HealthUserServiceRemote extends HealthUserServiceLocal {

    HealthUserAccessHistoryResponseDTO findHealthUserAccessHistory(String healthUserCi, Integer pageIndex,
            Integer pageSize);
}
