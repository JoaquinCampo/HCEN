package grupo12.practico.models;

import java.util.Set;
import java.util.stream.Collectors;

import grupo12.practico.dtos.HealthUser.HealthUserDTO;

public class HealthUser extends User {
    private ClinicalHistory clinicalHistory;
    private Set<Clinic> clinics;

    public HealthUser() {
        super();
        this.clinicalHistory = new ClinicalHistory();
        this.clinicalHistory.setHealthUser(this);
    }

    public ClinicalHistory getClinicalHistory() {
        return clinicalHistory;
    }

    public void setClinicalHistory(ClinicalHistory clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    public Set<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
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
        dto.setClinicalHistoryId(clinicalHistory != null ? clinicalHistory.getId() : null);
        dto.setClinicIds(clinics != null ? clinics.stream().map(Clinic::getId).collect(Collectors.toSet()) : null);
        return dto;
    }
}
