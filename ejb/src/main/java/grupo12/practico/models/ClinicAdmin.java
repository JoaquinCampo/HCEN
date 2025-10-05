package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;

import java.util.stream.Collectors;

import grupo12.practico.dtos.ClinicAdmin.ClinicAdminDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "clinic_admins")
public class ClinicAdmin extends User {
    @ManyToMany
    @JoinTable(name = "clinic_admin_clinic", joinColumns = @JoinColumn(name = "clinic_admin_id"), inverseJoinColumns = @JoinColumn(name = "clinic_id"))
    private Set<Clinic> clinics;

    public ClinicAdmin() {
        super();
        this.clinics = new HashSet<>();
    }

    public Set<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
    }

    public ClinicAdminDTO toDto() {
        ClinicAdminDTO dto = new ClinicAdminDTO();
        dto.setId(getId());
        dto.setDocument(getDocument());
        dto.setDocumentType(getDocumentType());
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setGender(getGender());
        dto.setEmail(getEmail());
        dto.setPhone(getPhone());
        dto.setImageUrl(getImageUrl());
        dto.setAddress(getAddress());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        dto.setClinicIds(clinics != null ? clinics.stream().map(Clinic::getId).collect(Collectors.toSet()) : null);
        return dto;
    }
}
