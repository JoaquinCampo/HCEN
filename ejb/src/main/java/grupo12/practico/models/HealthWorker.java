package grupo12.practico.models;

import java.util.Set;

import java.util.stream.Collectors;
import grupo12.practico.dto.HealthWorkerDTO;

public class HealthWorker extends User {
    private Set<Clinic> clinics;
    private Set<Specialty> specialties;
    private Set<ClinicalHistory> clinicalHistories;
    private Set<ClinicalDocument> clinicalDocuments;

    private String licenseNumber;

    public HealthWorker() {
        super();
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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