package grupo12.practico.dtos.ClinicalHistory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class ClinicalHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String healthUserId;
    private Set<String> clinicalDocumentIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public Set<String> getClinicalDocumentIds() {
        return clinicalDocumentIds;
    }

    public void setClinicalDocumentIds(Set<String> clinicalDocumentIds) {
        this.clinicalDocumentIds = clinicalDocumentIds;
    }
}
