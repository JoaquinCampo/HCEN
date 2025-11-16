package grupo12.practico.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "access_request_logs")
public class AccessRequestLog {
    @Id
    private String id;

    @Column(name = "access_request_id", nullable = false)
    private String accessRequestId;

    @Column(name = "health_user_ci", nullable = false)
    private String healthUserCi;

    @Column(name = "health_worker_ci", nullable = false)
    private String healthWorkerCi;

    @Column(name = "clinic_name", nullable = false)
    private String clinicName;

    @ElementCollection
    @CollectionTable(name = "access_request_log_specialties", joinColumns = @JoinColumn(name = "log_id"))
    @Column(name = "specialty_name")
    private List<String> specialtyNames;

    @Column(name = "action", nullable = false)
    private String action; // "REQUESTED", "ACCEPTED_BY_CLINIC", "ACCEPTED_BY_HEALTH_WORKER", "ACCEPTED_BY_SPECIALTY", "DENIED"

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public AccessRequestLog() {
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

    public String getAccessRequestId() {
        return accessRequestId;
    }

    public void setAccessRequestId(String accessRequestId) {
        this.accessRequestId = accessRequestId;
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

    public List<String> getSpecialtyNames() {
        return specialtyNames;
    }

    public void setSpecialtyNames(List<String> specialtyNames) {
        this.specialtyNames = specialtyNames;
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

