package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "health_users")
public class HealthUser extends User {
    @ElementCollection
    @CollectionTable(name = "health_user_clinic", joinColumns = @JoinColumn(name = "health_user_id"))
    @Column(name = "clinic_name")
    private Set<String> clinicNames;

    public HealthUser() {
        super();
        this.clinicNames = new HashSet<>();
    }

    public Set<String> getClinicNames() {
        return clinicNames;
    }

    public void setClinicNames(Set<String> clinicNames) {
        this.clinicNames = clinicNames;
    }

    public HealthUserDTO toDto() {
        HealthUserDTO dto = new HealthUserDTO();
        dto.setId(getId());
        dto.setCi(getCi());
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setGender(getGender());
        dto.setEmail(getEmail());
        dto.setPhone(getPhone());
        dto.setAddress(getAddress());
        dto.setDateOfBirth(getDateOfBirth());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        dto.setClinicNames(clinicNames);
        return dto;
    }
}
