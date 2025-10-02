package grupo12.practico.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import grupo12.practico.dtos.ClinicalDocument.ClinicalDocumentDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "clinical_documents")
public class ClinicalDocument {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content_url", length = 500)
    private String contentUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "clinical_history_id")
    private ClinicalHistory clinicalHistory;

    @ManyToMany
    @JoinTable(name = "clinical_document_health_worker", joinColumns = @JoinColumn(name = "clinical_document_id"), inverseJoinColumns = @JoinColumn(name = "health_worker_id"))
    private Set<HealthWorker> healthWorkers;

    public ClinicalDocument() {
        this.healthWorkers = new HashSet<>();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
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
