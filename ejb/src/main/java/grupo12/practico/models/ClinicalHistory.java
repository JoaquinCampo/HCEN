package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import grupo12.practico.dto.ClinicalHistoryDTO;

public class ClinicalHistory {
    private String id;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public ClinicalHistory() {
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
        // patientId will be filled where we have the association available
        return dto;
    }
}
