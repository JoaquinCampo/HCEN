package grupo12.practico.dtos.AccessPolicy;

import java.io.Serializable;
import java.time.LocalDate;

import grupo12.practico.dtos.Clinic.ClinicDTO;

public class ClinicAccessPolicyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String healthUserId;
    
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

    public ClinicDTO getClinic() {
        return clinic;
    }

    public void setClinic(ClinicDTO clinic) {
        this.clinic = clinic;
    }
}
