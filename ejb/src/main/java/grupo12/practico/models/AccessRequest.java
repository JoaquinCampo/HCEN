package grupo12.practico.models;

import java.time.LocalDate;
import java.util.UUID;

import grupo12.practico.dtos.AccessRequest.AccessRequestDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "access_requests", uniqueConstraints = {
        @UniqueConstraint(name = "uq_access_request_health_user_worker_clinic_specialty", columnNames = {
                "health_user_id",
                "health_worker_id",
                "clinic_id",
                "specialty_id"
        })
})
public class AccessRequest {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_user_id", nullable = false)
    private HealthUser healthUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_worker_id", nullable = false)
    private HealthWorker healthWorker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccessRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    public AccessRequest() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.status == null) {
            this.status = AccessRequestStatus.PENDING;
        }
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
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

    public HealthWorker getHealthWorker() {
        return healthWorker;
    }

    public void setHealthWorker(HealthWorker healthWorker) {
        this.healthWorker = healthWorker;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public AccessRequestStatus getStatus() {
        return status;
    }

    public void setStatus(AccessRequestStatus status) {
        this.status = status;
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

    public AccessRequestDTO toDto() {
        AccessRequestDTO dto = new AccessRequestDTO();
        dto.setId(id);
        dto.setHealthUserId(healthUser != null ? healthUser.getId() : null);
        dto.setHealthWorkerId(healthWorker != null ? healthWorker.getId() : null);
        dto.setHealthWorkerName(
                healthWorker != null ? healthWorker.getFirstName() + " " + healthWorker.getLastName() : null);
        dto.setClinicId(clinic != null ? clinic.getId() : null);
        dto.setClinicName(clinic != null ? clinic.getName() : null);
        dto.setSpecialtyId(specialty != null ? specialty.getId() : null);
        dto.setSpecialtyName(specialty != null ? specialty.getName() : null);
        dto.setStatus(status);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        return dto;
    }
}
