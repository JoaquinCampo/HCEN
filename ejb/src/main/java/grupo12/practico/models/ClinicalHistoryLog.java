package grupo12.practico.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clinical_history_logs")
public class ClinicalHistoryLog {
    @Id
    private String id;

    @Column(name = "health_user_ci", nullable = false)
    private String healthUserCi;

    @Column(name = "accessor_ci")
    private String accessorCi; // CI of the person accessing (health worker or health user themselves)

    @Column(name = "accessor_type", nullable = false)
    private String accessorType; // "HEALTH_USER" or "HEALTH_WORKER"

    @Column(name = "clinic_name")
    private String clinicName;

    @ElementCollection
    @CollectionTable(name = "clinical_history_log_specialties", joinColumns = @JoinColumn(name = "log_id"))
    @Column(name = "specialty_name")
    private List<String> specialtyNames;

    @Column(name = "access_type")
    private String accessType; // "BY_CLINIC", "BY_HEALTH_WORKER", "BY_SPECIALTY", "SELF_ACCESS"

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public ClinicalHistoryLog() {
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

    public String getHealthUserCi() {
        return healthUserCi;
    }

    public void setHealthUserCi(String healthUserCi) {
        this.healthUserCi = healthUserCi;
    }

    public String getAccessorCi() {
        return accessorCi;
    }

    public void setAccessorCi(String accessorCi) {
        this.accessorCi = accessorCi;
    }

    public String getAccessorType() {
        return accessorType;
    }

    public void setAccessorType(String accessorType) {
        this.accessorType = accessorType;
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

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
