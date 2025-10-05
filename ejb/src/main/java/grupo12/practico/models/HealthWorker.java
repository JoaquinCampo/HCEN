package grupo12.practico.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "health_workers")
public class HealthWorker extends User {
    @ManyToMany
    @JoinTable(name = "health_worker_clinic", joinColumns = @JoinColumn(name = "health_worker_id"), inverseJoinColumns = @JoinColumn(name = "clinic_id"))
    private Set<Clinic> clinics;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "health_worker_specialty", joinColumns = @JoinColumn(name = "health_worker_id"), inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    private Set<Specialty> specialties;

    @ManyToMany(mappedBy = "healthWorkers")
    private Set<ClinicalHistory> clinicalHistories;

    @ManyToMany(mappedBy = "healthWorkers")
    private Set<ClinicalDocument> clinicalDocuments;

    @Column(name = "license_number", length = 100)
    private String licenseNumber;

    public HealthWorker() {
        super();
        this.clinics = new HashSet<>();
        this.specialties = new HashSet<>();
        this.clinicalHistories = new HashSet<>();
        this.clinicalDocuments = new HashSet<>();
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
