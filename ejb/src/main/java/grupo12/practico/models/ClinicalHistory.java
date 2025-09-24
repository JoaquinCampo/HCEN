package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import grupo12.practico.dto.ClinicalHistoryDTO;

public class ClinicalHistory {
    private String id;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private HealthUser patient;
    private java.util.Set<ClinicalDocument> documents;

    public ClinicalHistory() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
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

    public HealthUser getPatient() {
        return patient;
    }

    public void setPatient(HealthUser patient) {
        this.patient = patient;
    }

    public java.util.Set<ClinicalDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(java.util.Set<ClinicalDocument> documents) {
        this.documents = documents;
    }

    public void addDocument(ClinicalDocument document) {
        if (this.documents == null) {
            this.documents = new java.util.HashSet<>();
        }
        this.documents.add(document);
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
        dto.setHealthUserId(patient != null ? patient.getId() : null);
        return dto;
    }
}
