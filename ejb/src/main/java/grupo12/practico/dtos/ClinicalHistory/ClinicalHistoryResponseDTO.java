package grupo12.practico.dtos.ClinicalHistory;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.ClinicalDocument.DocumentResponseDTO;
import java.util.List;

public class ClinicalHistoryResponseDTO {
    private HealthUserDTO healthUser;
    private List<DocumentResponseDTO> documents;

    public HealthUserDTO getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUserDTO healthUser) {
        this.healthUser = healthUser;
    }

    public List<DocumentResponseDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentResponseDTO> documents) {
        this.documents = documents;
    }
}