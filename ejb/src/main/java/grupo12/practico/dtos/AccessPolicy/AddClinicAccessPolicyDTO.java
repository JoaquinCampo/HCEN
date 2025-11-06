package grupo12.practico.dtos.AccessPolicy;

import java.io.Serializable;

public class AddClinicAccessPolicyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String healthUserId;
    private String clinicName;
    private String accessRequestId;

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getAccessRequestId() {
        return accessRequestId;
    }

    public void setAccessRequestId(String accessRequestId) {
        this.accessRequestId = accessRequestId;
    }
}
