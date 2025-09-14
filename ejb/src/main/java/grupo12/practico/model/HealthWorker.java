package grupo12.practico.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class HealthWorker {
    private String id;
    private String firstName;
    private String lastName;
    private String dni;
    private Gender gender;
    private String specialty;
    private String licenseNumber;
    private LocalDate hireDate;

    private Set<User> patients = new HashSet<>();
    private Set<HealthProvider> healthProviders = new HashSet<>();

    public HealthWorker() {
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Set<User> getPatients() {
        return patients;
    }

    public void setPatients(Set<User> patients) {
        this.patients = patients != null ? patients : new HashSet<>();
    }

    public Set<HealthProvider> getHealthProviders() {
        return healthProviders;
    }

    public void setHealthProviders(Set<HealthProvider> healthProviders) {
        this.healthProviders = healthProviders != null ? healthProviders : new HashSet<>();
    }

    public void addPatient(User patient) {
        if (patient == null) {
            return;
        }
        if (this.patients.add(patient)) {
            patient.addHealthWorker(this);
        }
    }

    public void removePatient(User patient) {
        if (patient == null) {
            return;
        }
        if (this.patients.remove(patient)) {
            patient.removeHealthWorker(this);
        }
    }

    public void addHealthProvider(HealthProvider healthProvider) {
        if (healthProvider == null) {
            return;
        }
        if (this.healthProviders.add(healthProvider)) {
            healthProvider.addHealthWorker(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        HealthWorker that = (HealthWorker) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
