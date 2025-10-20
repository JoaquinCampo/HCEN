package grupo12.practico.models;

import java.time.LocalDate;
import java.util.UUID;

import grupo12.practico.dtos.AccessPolicy.SpecialtyAccessPolicyDTO;
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
@Table(name = "specialty_access_policies", uniqueConstraints = {
        @UniqueConstraint(name = "uq_health_user_specialty_policy", columnNames = {
                "health_user_id",
                "specialty_id"
        })
})
public class SpecialtyAccessPolicy {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "health_user_id", nullable = false)
    private HealthUser healthUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "granted_at", nullable = false)
    private LocalDate grantedAt;

    public SpecialtyAccessPolicy() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.grantedAt = LocalDate.now();
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

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public LocalDate getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDate grantedAt) {
        this.grantedAt = grantedAt;
    }

    public SpecialtyAccessPolicyDTO toDto() {
        SpecialtyAccessPolicyDTO dto = new SpecialtyAccessPolicyDTO();
        dto.setId(id);
        dto.setHealthUserId(healthUser != null ? healthUser.getId() : null);
        dto.setSpecialtyId(specialty != null ? specialty.getId() : null);
        dto.setGrantedAt(grantedAt);
        return dto;
    }
}
