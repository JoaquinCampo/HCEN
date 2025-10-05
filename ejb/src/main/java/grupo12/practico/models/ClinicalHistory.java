package grupo12.practico.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Set;

import grupo12.practico.dtos.ClinicalHistory.ClinicalHistoryDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "clinical_histories")
public class ClinicalHistory {
    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @OneToOne
    @JoinColumn(name = "health_user_id", unique = true)
    private HealthUser healthUser;

    @OneToMany(mappedBy = "clinicalHistory", cascade = CascadeType.ALL)
    private Set<ClinicalDocument> clinicalDocuments;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @ManyToMany
    @JoinTable(name = "clinical_history_health_worker", joinColumns = @JoinColumn(name = "clinical_history_id"), inverseJoinColumns = @JoinColumn(name = "health_worker_id"))
    private Set<HealthWorker> healthWorkers;

    public ClinicalHistory() {
        this.clinicalDocuments = new HashSet<>();
        this.healthWorkers = new HashSet<>();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
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

    public HealthUser getHealthUser() {
        return healthUser;
    }

    public void setHealthUser(HealthUser healthUser) {
        this.healthUser = healthUser;
    }

    public Set<ClinicalDocument> getClinicalDocuments() {
        return clinicalDocuments;
    }

    public void setClinicalDocuments(Set<ClinicalDocument> clinicalDocuments) {
        this.clinicalDocuments = clinicalDocuments;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClinicalHistory that = (ClinicalHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ClinicalHistory{" +
                "id='" + id + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public ClinicalHistoryDTO toDto() {
        ClinicalHistoryDTO dto = new ClinicalHistoryDTO();
        dto.setId(id);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        dto.setHealthUserId(healthUser != null ? healthUser.getId() : null);
        dto.setClinicalDocumentIds(clinicalDocuments != null
                ? clinicalDocuments.stream().map(ClinicalDocument::getId).collect(Collectors.toSet())
                : null);
        return dto;
    }
}
