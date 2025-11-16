package grupo12.practico.dtos.ClinicalHistory;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import java.util.List;

public class ClinicalHistoryResponseDTO {
    private HealthUserDTO healthUser;
    private List<ClinicalDocumentDTO> documents;
    private boolean hasAccess;
    private String accessMessage;

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

    public boolean isHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getAccessMessage() {
        return accessMessage;
    }

    public void setAccessMessage(String accessMessage) {
        this.accessMessage = accessMessage;
    }
}