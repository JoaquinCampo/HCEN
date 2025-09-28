package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;

public class ClinicalDocument {
    private String id;
    private String title;
    private String contentUrl;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private ClinicalHistory clinicalHistory;
    private Set<HealthWorker> healthWorkers;

    public ClinicalDocument() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
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

    public ClinicalHistory getClinicalHistory() {
        return clinicalHistory;
    }

    public void setClinicalHistory(ClinicalHistory clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClinicalDocument that = (ClinicalDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ClinicalDocument{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public ClinicalDocumentDTO toDto() {
        ClinicalDocumentDTO dto = new ClinicalDocumentDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setContentUrl(contentUrl);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        dto.setClinicalHistoryId(clinicalHistory != null ? clinicalHistory.getId() : null);
        dto.setHealthWorkerIds(
                healthWorkers != null ? healthWorkers.stream().map(HealthWorker::getId).collect(Collectors.toSet())
                        : null);
        return dto;
    }
}
