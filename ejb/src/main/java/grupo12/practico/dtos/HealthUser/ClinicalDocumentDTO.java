package grupo12.practico.dtos.HealthUser;

import java.time.LocalDate;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

public class ClinicalDocumentDTO {
    private ClinicDTO clinic;
    private HealthWorkerDTO healthWorker;
    private String title;
    private String description;
    private String content;
    private String contentType;
    private String contentUrl;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public ClinicalDocumentDTO() {
    }

    public ClinicDTO getClinic() {
        return clinic;
    }

    public void setClinic(ClinicDTO clinic) {
        this.clinic = clinic;
    }

    public HealthWorkerDTO getHealthWorker() {
        return healthWorker;
    }

    public void setHealthWorker(HealthWorkerDTO healthWorker) {
        this.healthWorker = healthWorker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
}
