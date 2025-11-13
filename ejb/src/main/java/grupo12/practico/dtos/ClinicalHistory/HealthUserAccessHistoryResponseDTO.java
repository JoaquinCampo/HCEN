package grupo12.practico.dtos.ClinicalHistory;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import java.util.List;

public class HealthUserAccessHistoryResponseDTO {
    private HealthUserDTO healthUser;
    private List<ClinicalHistoryAccessLogResponseDTO> accessHistory;

    public HealthUserAccessHistoryResponseDTO() {
    }

    public HealthUserAccessHistoryResponseDTO(HealthUserDTO healthUser,
            List<ClinicalHistoryAccessLogResponseDTO> accessHistory) {
        this.healthUser = healthUser;
        this.accessHistory = accessHistory;
    }

    public HealthUserDTO getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUserDTO healthUser) {
        this.healthUser = healthUser;
    }

    public List<ClinicalHistoryAccessLogResponseDTO> getAccessHistory() {
        return accessHistory;
    }

    public void setAccessHistory(List<ClinicalHistoryAccessLogResponseDTO> accessHistory) {
        this.accessHistory = accessHistory;
    }
}