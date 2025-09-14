package grupo12.practico.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class HealthProvider {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String registrationNumber;
    private LocalDate registrationDate;
    private boolean active;

    private Set<User> attendedPatients = new HashSet<>();
    private Set<User> affiliatedPatients = new HashSet<>();
    private Set<HealthWorker> healthWorkers = new HashSet<>();

    public HealthProvider() {
        this.id = UUID.randomUUID().toString();
        this.active = true;
        this.registrationDate = LocalDate.now();
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // =======================================================
    // RELATIONSHIP GETTERS AND SETTERS
    // =======================================================

    public Set<User> getAttendedPatients() {
        return attendedPatients;
    }

    public void setAttendedPatients(Set<User> attendedPatients) {
        this.attendedPatients = attendedPatients != null ? attendedPatients : new HashSet<>();
    }

    public Set<User> getAffiliatedPatients() {
        return affiliatedPatients;
    }

    public void setAffiliatedPatients(Set<User> affiliatedPatients) {
        this.affiliatedPatients = affiliatedPatients != null ? affiliatedPatients : new HashSet<>();
    }

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(Set<HealthWorker> healthWorkers) {
        this.healthWorkers = healthWorkers != null ? healthWorkers : new HashSet<>();
    }

    // =======================================================
    // BIDIRECTIONAL RELATIONSHIP MANAGEMENT METHODS
    // =======================================================

    // For attended patients relationship
    public void addAttendedPatient(User patient) {
        if (patient == null) {
            return;
        }
        if (this.attendedPatients.add(patient)) {
            patient.addAttendedHealthProvider(this);
        }
    }

    public void removeAttendedPatient(User patient) {
        if (patient == null) {
            return;
        }
        if (this.attendedPatients.remove(patient)) {
            patient.removeAttendedHealthProvider(this);
        }
    }

    // For affiliated patients relationship
    public void addAffiliatedPatient(User patient) {
        if (patient == null) {
            return;
        }
        if (this.affiliatedPatients.add(patient)) {
            patient.addAffiliatedHealthProvider(this);
        }
    }

    public void removeAffiliatedPatient(User patient) {
        if (patient == null) {
            return;
        }
        if (this.affiliatedPatients.remove(patient)) {
            patient.removeAffiliatedHealthProvider(this);
        }
    }

    // For health workers relationship
    public void addHealthWorker(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return;
        }
        if (this.healthWorkers.add(healthWorker)) {
            healthWorker.addHealthProvider(this);
        }
    }

    public void removeHealthWorker(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return;
        }
        if (this.healthWorkers.remove(healthWorker)) {
            healthWorker.removeHealthProvider(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HealthProvider that = (HealthProvider) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "HealthProvider{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
