package grupo12.practico.dtos.AccessRequest;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import grupo12.practico.dtos.HealthWorker.HealthWorkerDTO;
import grupo12.practico.dtos.Clinic.ClinicDTO;

public class AccessRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String healthUserId;
    private String healthUserCi;
    private List<String> specialtyNames;

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

    public HealthWorkerDTO getHealthWorker() {
        return healthWorker;
    }

    public void setHealthWorker(HealthWorkerDTO healthWorker) {
        this.healthWorker = healthWorker;
    }

    public String getHealthUserCi() {
        return healthUserCi;
    }

    public void setHealthUserCi(String healthUserCi) {
        this.healthUserCi = healthUserCi;
    }

    public ClinicDTO getClinic() {
        return clinic;
    }

    public void setClinic(ClinicDTO clinic) {
        this.clinic = clinic;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getSpecialtyNames() {
        return specialtyNames;
    }

    public void setSpecialtyNames(List<String> specialtyNames) {
        this.specialtyNames = specialtyNames;
    }

}
