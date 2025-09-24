package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import grupo12.practico.dto.HealthUserDTO;

public class HealthUser extends User {
    private ClinicalHistory clinicalHistory;
    private Set<Clinic> clinics;
    private Set<HealthWorker> healthWorkers;

    public HealthUser() {
        super();
        // ClinicalHistory is now optional - created lazily when needed
    }

    public ClinicalHistory getClinicalHistory() {
        return clinicalHistory;
    }

    public void setClinicalHistory(ClinicalHistory clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    /**
     * Gets the clinical history, creating it if it doesn't exist.
     * 
     * @return the clinical history for this user
     */
    public ClinicalHistory getOrCreateClinicalHistory() {
        if (this.clinicalHistory == null) {
            this.clinicalHistory = new ClinicalHistory();
            this.clinicalHistory.setPatient(this);
        }
        return this.clinicalHistory;
    }

    public Set<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
    }

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }

    @Override
    public void addHealthWorker(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return;
        }
        if (this.healthWorkers == null) {
            this.healthWorkers = new HashSet<>();
        }
        if (this.healthWorkers.add(healthWorker)) {
            healthWorker.addPatient(this);
        }
    }

    @Override
    public void addAffiliatedHealthProvider(Clinic clinic) {
        if (clinic == null) {
            return;
        }
        if (this.clinics == null) {
            this.clinics = new HashSet<>();
        }
        if (this.clinics.add(clinic)) {
            clinic.addHealthUser(this);
        }
    }

    public HealthUserDTO toDto() {
        HealthUserDTO dto = new HealthUserDTO();
        // base fields
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
        // specific
        dto.setClinicalHistoryId(clinicalHistory != null ? clinicalHistory.getId() : null);
        dto.setClinicIds(clinics != null ? clinics.stream().map(Clinic::getId).collect(Collectors.toSet()) : null);
        return dto;
    }
}
