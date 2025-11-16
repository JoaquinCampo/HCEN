package grupo12.practico.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "health_user_logs")
public class HealthUserLog {
    @Id
    private String id;

    @Column(name = "health_user_ci", nullable = false)
    private String healthUserCi;

    @Column(name = "action", nullable = false)
    private String action; // "CREATED" or "CLINIC_LINKED"

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public HealthUserLog() {
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

