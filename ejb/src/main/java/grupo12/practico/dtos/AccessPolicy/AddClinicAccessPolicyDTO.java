package grupo12.practico.dtos.AccessPolicy;

import java.io.Serializable;

public class AddClinicAccessPolicyDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String healthUserCi;
    private String clinicName;
    private String accessRequestId;

    public String getHealthUserCi() {
        return healthUserCi;
    }

    public void setHealthUserCi(String healthUserCi) {
        this.healthUserCi = healthUserCi;
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
