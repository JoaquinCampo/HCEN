package grupo12.practico.models;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import grupo12.practico.dtos.Clinic.ClinicDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "clinics")
public class Clinic {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    @ManyToMany(mappedBy = "clinics")
    private Set<HealthUser> healthUsers;

    @ManyToMany(mappedBy = "clinics")
    private Set<HealthWorker> healthWorkers;

    @ManyToMany(mappedBy = "clinics")
    private Set<ClinicAdmin> clinicAdmins;

    public Clinic() {
        this.healthUsers = new HashSet<>();
        this.healthWorkers = new HashSet<>();
        this.clinicAdmins = new HashSet<>();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
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
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public ClinicDTO toDto() {
        ClinicDTO dto = new ClinicDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setAddress(address);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        return dto;
    }
}
