package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "health_users")
public class HealthUser extends User {
    @ManyToMany
    @JoinTable(name = "health_user_clinic", joinColumns = @JoinColumn(name = "health_user_id"), inverseJoinColumns = @JoinColumn(name = "clinic_id"))
    private Set<Clinic> clinics;

    @OneToMany(mappedBy = "healthUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ClinicalDocument> clinicalDocuments;

    public HealthUser() {
        super();
        this.clinics = new HashSet<>();
        this.clinicalDocuments = new HashSet<>();
    }

    public Set<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
    }

    public Set<ClinicalDocument> getClinicalDocuments() {
        return clinicalDocuments;
    }

    public void setClinicalDocuments(Set<ClinicalDocument> clinicalDocuments) {
        this.clinicalDocuments = clinicalDocuments;
    }

    public HealthUserDTO toDto() {
        HealthUserDTO dto = new HealthUserDTO();
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
        dto.setDateOfBirth(getDateOfBirth());
        dto.setCreatedAt(getCreatedAt());
        dto.setUpdatedAt(getUpdatedAt());
        dto.setClinicIds(clinics != null ? clinics.stream().map(Clinic::getId).collect(Collectors.toSet()) : null);
        dto.setClinicalDocumentIds(
                clinicalDocuments != null ? clinicalDocuments.stream().map(ClinicalDocument::getId).collect(Collectors.toSet())
                        : null);
        return dto;
    }
}
