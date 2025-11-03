package grupo12.practico.dtos.HealthUser;

import java.io.Serializable;

import java.util.List;

/**
 * Simple DTO that wraps the clinical history payload returned by the external
 * service.
 */
public class ClinicalHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private HealthUserDTO healthUser;
    private List<ClinicalDocumentDTO> clinicalDocuments;

    public ClinicalHistoryDTO() {
    }

    public HealthUserDTO getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUserDTO healthUser) {
        this.healthUser = healthUser;
    }

    public List<ClinicalDocumentDTO> getClinicalDocuments() {
        return clinicalDocuments;
    }

    public void setClinicalDocuments(List<ClinicalDocumentDTO> clinicalDocuments) {
        this.clinicalDocuments = clinicalDocuments;
    }
}
