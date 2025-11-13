package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;
import java.time.LocalDateTime;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

public class DocumentResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private HealthWorkerDTO healthWorker;
    private ClinicDTO clinic;
    private LocalDateTime createdAt;
    private String s3Url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HealthWorkerDTO getHealthWorker() {
        return healthWorker;
    }

    public void setHealthWorker(HealthWorkerDTO healthWorker) {
        this.healthWorker = healthWorker;
    }

    public ClinicDTO getClinic() {
        return clinic;
    }

    public void setClinic(ClinicDTO clinic) {
        this.clinic = clinic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
}
