package grupo12.practico.dtos.AccessPolicy;

import java.io.Serializable;
import java.time.LocalDate;

public class ClinicAccessPolicyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String healthUserId;
    private String clinicId;
    private LocalDate grantedAt;

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

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
    }

    public LocalDate getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDate grantedAt) {
        this.grantedAt = grantedAt;
    }
}
