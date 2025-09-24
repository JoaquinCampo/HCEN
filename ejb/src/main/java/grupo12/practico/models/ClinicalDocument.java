package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import grupo12.practico.dto.ClinicalDocumentDTO;

public class ClinicalDocument {
    private String id;
    private String title;
    private String content;
    private String contentUrl;
    private LocalDate issuedAt;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private ClinicalHistory clinicalHistory;
    private HealthWorker author;
    private Clinic provider;
    private Set<HealthWorker> healthWorkers;

    public ClinicalDocument() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.issuedAt = LocalDate.now(); // Set issued date to creation date
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
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

    public HealthWorker getAuthor() {
        return author;
    }

    public void setAuthor(HealthWorker author) {
        this.author = author;
        // Also add to the healthWorkers set for consistency
        if (author != null) {
            if (this.healthWorkers == null) {
                this.healthWorkers = new java.util.HashSet<>();
            }
            this.healthWorkers.add(author);
        }
    }

    public Clinic getProvider() {
        return provider;
    }

    public void setProvider(Clinic provider) {
        this.provider = provider;
    }

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }

    public void addAuthor(HealthWorker author) {
        if (this.healthWorkers == null) {
            this.healthWorkers = new java.util.HashSet<>();
        }
        this.healthWorkers.add(author);
        // Set as primary author if not set
        if (this.author == null) {
            this.author = author;
        }
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
                ", issuedAt=" + issuedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public ClinicalDocumentDTO toDto() {
        ClinicalDocumentDTO dto = new ClinicalDocumentDTO();
        dto.setId(id);
        dto.setTitle(title);
        dto.setContent(content != null ? content : contentUrl);
        dto.setIssuedAt(issuedAt);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        dto.setClinicalHistoryId(clinicalHistory != null ? clinicalHistory.getId() : null);
        // Add author and provider IDs
        dto.setHealthWorkerIds(author != null ? java.util.Set.of(author.getId()) : null);
        dto.setClinicIds(provider != null ? java.util.Set.of(provider.getId()) : null);
        return dto;
    }
}
