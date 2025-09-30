package grupo12.practico.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryDTO;

public class ClinicalHistory {
    private String id;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private HealthUser healthUser;
    private java.util.Set<ClinicalDocument> clinicalDocuments;

    public ClinicalHistory() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.clinicalDocuments = new HashSet<>();
    }

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

    public HealthUser getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUser healthUser) {
        this.healthUser = healthUser;
    }

    public Set<ClinicalDocument> getClinicalDocuments() {
        return clinicalDocuments;
    }

    public void setClinicalDocuments(Set<ClinicalDocument> clinicalDocuments) {
        this.clinicalDocuments = clinicalDocuments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClinicalHistory that = (ClinicalHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ClinicalHistory{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public ClinicalHistoryDTO toDto() {
        ClinicalHistoryDTO dto = new ClinicalHistoryDTO();
        dto.setId(id);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        dto.setHealthUserId(healthUser != null ? healthUser.getId() : null);
        dto.setClinicalDocumentIds(clinicalDocuments != null
                ? clinicalDocuments.stream().map(ClinicalDocument::getId).collect(Collectors.toSet())
                : null);
        return dto;
    }
}
