package grupo12.practico.dtos.AccessPolicy;

import java.io.Serializable;
import java.time.LocalDate;

import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;

public class HealthWorkerAccessPolicyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String healthUserId;
    private HealthWorkerDTO healthWorker;
    private ClinicDTO clinic;

    private LocalDate createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public HealthWorkerDTO getHealthWorker() {
        return healthWorker;
    }

    public void setHealthWorker(HealthWorkerDTO healthWorker) {
        this.healthWorker = healthWorker;
    }

    public ClinicDTO getClinic() {
        return clinic;
    }

    public void setClinic(ClinicDTO clinic) {
        this.clinic = clinic;
    }
}
