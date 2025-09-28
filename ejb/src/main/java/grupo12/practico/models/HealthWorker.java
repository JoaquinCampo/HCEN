package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

public class HealthWorker extends User {
    private Set<Clinic> clinics;
    private Set<Specialty> specialties;
    private Set<ClinicalHistory> clinicalHistories;
    private Set<ClinicalDocument> clinicalDocuments;
    private Set<HealthUser> patients;

    private String licenseNumber;
    private java.time.LocalDate hireDate;

    public HealthWorker() {
        super();
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public java.time.LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(java.time.LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Set<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(Set<Clinic> clinics) {
        this.clinics = clinics;
    }

    public Set<Specialty> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(Set<Specialty> specialties) {
        this.specialties = specialties;
    }

    public Set<ClinicalHistory> getClinicalHistories() {
        return clinicalHistories;
    }

    public void setClinicalHistories(Set<ClinicalHistory> clinicalHistories) {
        this.clinicalHistories = clinicalHistories;
    }

    public Set<ClinicalDocument> getClinicalDocuments() {
        return clinicalDocuments;
    }

    public void setClinicalDocuments(Set<ClinicalDocument> clinicalDocuments) {
        this.clinicalDocuments = clinicalDocuments;
    }

    // Relationship methods
    public void addHealthProvider(Clinic clinic) {
        if (clinic == null) {
            return;
        }
        if (this.clinics == null) {
            this.clinics = new HashSet<>();
        }
        if (this.clinics.add(clinic)) {
            clinic.addHealthWorker(this);
        }
    }

    // Alias for getClinics for backwards compatibility
    public Set<Clinic> getHealthProviders() {
        return getClinics();
    }

    public void addAuthoredDocument(ClinicalDocument document) {
        if (this.clinicalDocuments == null) {
            this.clinicalDocuments = new HashSet<>();
        }
        this.clinicalDocuments.add(document);
    }

    public Set<HealthUser> getPatients() {
        return patients;
    }

    public void setPatients(Set<HealthUser> patients) {
        this.patients = patients;
    }

    public void addPatient(HealthUser healthUser) {
        if (healthUser == null) {
            return;
        }
        if (this.patients == null) {
            this.patients = new HashSet<>();
        }
        if (this.patients.add(healthUser)) {
            healthUser.addHealthWorker(this);
        }
    }

    public HealthWorkerDTO toDto() {
        HealthWorkerDTO dto = new HealthWorkerDTO();
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
        dto.setLicenseNumber(licenseNumber);
        dto.setClinicIds(clinics != null ? clinics.stream().map(Clinic::getId).collect(Collectors.toSet()) : null);
        dto.setSpecialtyIds(
                specialties != null ? specialties.stream().map(Specialty::getId).collect(Collectors.toSet()) : null);
        return dto;
    }
}
