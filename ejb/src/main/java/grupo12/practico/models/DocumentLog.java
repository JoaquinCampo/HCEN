package grupo12.practico.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "document_logs")
public class DocumentLog {
    @Id
    private String id;

    @Column(name = "document_id", nullable = false)
    private String documentId;

    @Column(name = "health_user_ci", nullable = false)
    private String healthUserCi;

    @Column(name = "health_worker_ci", nullable = false)
    private String healthWorkerCi;

    @Column(name = "clinic_name", nullable = false)
    private String clinicName;

    @Column(name = "action", nullable = false)
    private String action; // "CREATED"

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public DocumentLog() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getHealthUserCi() {
        return healthUserCi;
    }

    public void setHealthUserCi(String healthUserCi) {
        this.healthUserCi = healthUserCi;
    }

    public String getHealthWorkerCi() {
        return healthWorkerCi;
    }

    public void setHealthWorkerCi(String healthWorkerCi) {
        this.healthWorkerCi = healthWorkerCi;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

