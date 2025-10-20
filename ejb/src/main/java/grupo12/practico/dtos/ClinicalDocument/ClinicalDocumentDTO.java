package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;
import java.util.Set;
import java.time.LocalDate;

public class ClinicalDocumentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String title;
    private String contentUrl;
    private LocalDate issuedAt;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String healthUserId;
    private Set<String> healthWorkerIds;

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

    public LocalDate getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDate issuedAt) {
        this.issuedAt = issuedAt;
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

    public Set<String> getHealthWorkerIds() {
        return healthWorkerIds;
    }

    public void setHealthWorkerIds(Set<String> healthWorkerIds) {
        this.healthWorkerIds = healthWorkerIds;
    }
}
