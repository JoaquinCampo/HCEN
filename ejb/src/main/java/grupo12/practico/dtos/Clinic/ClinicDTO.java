package grupo12.practico.dtos.Clinic;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;

public class ClinicDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private List<HealthWorkerDTO> healthWorkers;

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

    public List<HealthWorkerDTO> getHealthWorkers() {
        return healthWorkers;
    }

    public void setHealthWorkers(List<HealthWorkerDTO> healthWorkers) {
        this.healthWorkers = healthWorkers;
    }
}
