package grupo12.practico.models;

import java.time.LocalDate;
import java.util.UUID;

import grupo12.practico.dtos.AccessPolicy.HealthWorkerAccessPolicyDTO;
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
@Table(name = "health_worker_access_policies", uniqueConstraints = {
        @UniqueConstraint(name = "uq_health_user_health_worker_policy", columnNames = {
                "health_user_id",
                "health_worker_id"
        })
})
public class HealthWorkerAccessPolicy {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_user_id", nullable = false)
    private HealthUser healthUser;

    @Column(name = "health_worker_ci", nullable = false)
    private String healthWorkerCi;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    public HealthWorkerAccessPolicy() {
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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public HealthWorkerAccessPolicyDTO toDto() {
        HealthWorkerAccessPolicyDTO dto = new HealthWorkerAccessPolicyDTO();
        dto.setId(id);
        dto.setHealthUserId(healthUser != null ? healthUser.getId() : null);
        dto.setHealthWorkerCi(healthWorkerCi);
        dto.setCreatedAt(createdAt);
        return dto;
    }
}
