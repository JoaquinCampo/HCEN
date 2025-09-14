package grupo12.practico.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String dni;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phone;
    private String address;

    private Set<HealthWorker> healthWorkers = new HashSet<>();
    private Set<HealthProvider> attendedHealthProviders = new HashSet<>();
    private Set<HealthProvider> affiliatedHealthProviders = new HashSet<>();

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
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

    public Set<HealthWorker> getHealthWorkers() {
        return healthWorkers;
    }

    public Set<HealthProvider> getAttendedHealthProviders() {
        return attendedHealthProviders;
    }

    public void setAttendedHealthProviders(Set<HealthProvider> attendedHealthProviders) {
        this.attendedHealthProviders = attendedHealthProviders != null ? attendedHealthProviders : new HashSet<>();
    }

    public Set<HealthProvider> getAffiliatedHealthProviders() {
        return affiliatedHealthProviders;
    }

    public void setAffiliatedHealthProviders(Set<HealthProvider> affiliatedHealthProviders) {
        this.affiliatedHealthProviders = affiliatedHealthProviders != null ? affiliatedHealthProviders
                : new HashSet<>();
    }

    public void addHealthWorker(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return;
        }
        if (this.healthWorkers.add(healthWorker)) {
            healthWorker.addPatient(this);
        }
    }

    public void removeHealthWorker(HealthWorker healthWorker) {
        if (healthWorker == null) {
            return;
        }
        if (this.healthWorkers.remove(healthWorker)) {
            healthWorker.removePatient(this);
        }
    }

    // HealthProvider relationship management methods
    public void addAttendedHealthProvider(HealthProvider healthProvider) {
        if (healthProvider == null) {
            return;
        }
        if (this.attendedHealthProviders.add(healthProvider)) {
            healthProvider.addAttendedPatient(this);
        }
    }

    public void removeAttendedHealthProvider(HealthProvider healthProvider) {
        if (healthProvider == null) {
            return;
        }
        if (this.attendedHealthProviders.remove(healthProvider)) {
            healthProvider.removeAttendedPatient(this);
        }
    }

    public void addAffiliatedHealthProvider(HealthProvider healthProvider) {
        if (healthProvider == null) {
            return;
        }
        if (this.affiliatedHealthProviders.add(healthProvider)) {
            healthProvider.addAffiliatedPatient(this);
        }
    }

    public void removeAffiliatedHealthProvider(HealthProvider healthProvider) {
        if (healthProvider == null) {
            return;
        }
        if (this.affiliatedHealthProviders.remove(healthProvider)) {
            healthProvider.removeAffiliatedPatient(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
