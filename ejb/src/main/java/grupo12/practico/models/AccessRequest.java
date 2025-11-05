package grupo12.practico.models;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "access_requests", uniqueConstraints = {
        @UniqueConstraint(name = "uq_access_request_health_user_worker_clinic", columnNames = {
                "health_user_id",
                "health_worker_ci",
                "clinic_name"
        })
})
public class AccessRequest {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_user_id", nullable = false)
    private HealthUser healthUser;

    @Column(name = "health_worker_ci", nullable = false)
    private String healthWorkerCi;

    @Column(name = "clinic_name", nullable = false)
    private String clinicName;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    public AccessRequest() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HealthUser getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUser healthUser) {
        this.healthUser = healthUser;
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
