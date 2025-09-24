package grupo12.practico.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import grupo12.practico.dto.ClinicDTO;

public class Clinic {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String domain;
    private String registrationNumber;
    private LocalDate registrationDate;
    private ClinicType type;
    private boolean active;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    private Set<HealthUser> healthUsers;
    private Set<HealthWorker> healthWorkers;
    private Set<ClinicAdmin> clinicAdmins;
    private Set<ClinicalHistory> clinicalHistories;

    public Clinic() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.active = true; // Clinics are active by default
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ClinicType getType() {
        return type;
    }

    public void setType(ClinicType type) {
        this.type = type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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

    public Set<HealthUser> getHealthUsers() {
        return healthUsers;
    }

    public void setHealthUsers(Set<HealthUser> healthUsers) {
        this.healthUsers = healthUsers;
    }

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }

    public Set<ClinicAdmin> getClinicAdmins() {
        return clinicAdmins;
    }

    public void setClinicAdmins(Set<ClinicAdmin> clinicAdmins) {
        this.clinicAdmins = clinicAdmins;
    }

    public Set<ClinicalHistory> getClinicalHistories() {
        return clinicalHistories;
    }

    public void setClinicalHistories(Set<ClinicalHistory> clinicalHistories) {
        this.clinicalHistories = clinicalHistories;
    }

    // Relationship methods
    public void addHealthWorker(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return;
        }
        if (this.healthWorkers == null) {
            this.healthWorkers = new HashSet<>();
        }
        if (this.healthWorkers.add(healthWorker)) {
            healthWorker.addHealthProvider(this);
        }
    }

    public void addClinicalDocument(ClinicalDocument document) {
        if (this.clinicalHistories == null) {
            this.clinicalHistories = new java.util.HashSet<>();
        }
        // Add document to clinical history if it exists
        if (document != null && document.getClinicalHistory() != null) {
            this.clinicalHistories.add(document.getClinicalHistory());
        }
    }

    public void addHealthUser(HealthUser healthUser) {
        if (healthUser == null) {
            return;
        }
        if (this.healthUsers == null) {
            this.healthUsers = new HashSet<>();
        }
        if (this.healthUsers.add(healthUser)) {
            healthUser.addAffiliatedHealthProvider(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Clinic that = (Clinic) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Clinic{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", domain='" + domain + '\'' +
                ", type=" + type +
                ", active=" + active +
                '}';
    }

    public ClinicDTO toDto() {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setAddress(address);
        dto.setDomain(domain);
        dto.setType(type != null ? type.name() : null);
        // Note: active field not in DTO currently
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        return dto;
    }
}
