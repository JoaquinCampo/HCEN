package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import grupo12.practico.dtos.SpecialtyDTO;

public class Specialty {
    private String id;
    private String name;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private Set<HealthWorker> healthWorkers;
    private Set<ClinicalHistory> clinicalHistories;

    public Specialty() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }

    public Set<ClinicalHistory> getClinicalHistories() {
        return clinicalHistories;
    }

    public void setClinicalHistories(Set<ClinicalHistory> clinicalHistories) {
        this.clinicalHistories = clinicalHistories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Specialty that = (Specialty) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Specialty{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public SpecialtyDTO toDto() {
        SpecialtyDTO dto = new SpecialtyDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        return dto;
    }
}
