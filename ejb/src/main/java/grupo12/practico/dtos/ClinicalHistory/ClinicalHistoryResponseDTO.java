package grupo12.practico.dtos.ClinicalHistory;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import java.util.List;

public class ClinicalHistoryResponseDTO {
    private HealthUserDTO healthUser;
    private List<ClinicalDocumentDTO> documents;

    public HealthUserDTO getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUserDTO healthUser) {
        this.healthUser = healthUser;
    }

    public List<ClinicalDocumentDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ClinicalDocumentDTO> documents) {
        this.documents = documents;
    }
}