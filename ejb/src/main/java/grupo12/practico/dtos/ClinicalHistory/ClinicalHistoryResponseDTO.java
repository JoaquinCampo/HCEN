package grupo12.practico.dtos.ClinicalHistory;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import java.io.Serializable;
import java.util.List;

public class ClinicalHistoryResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private HealthUserDTO healthUser;
    private List<ClinicalDocumentDTO> documents;
    private Boolean hasAccess;
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

    public Boolean getHasAccess() {
        return hasAccess;
    }

    public void setHasAccess(Boolean hasAccess) {
        this.hasAccess = hasAccess;
    }

    public String getAccessMessage() {
        return accessMessage;
    }

    public void setAccessMessage(String accessMessage) {
        this.accessMessage = accessMessage;
    }
}