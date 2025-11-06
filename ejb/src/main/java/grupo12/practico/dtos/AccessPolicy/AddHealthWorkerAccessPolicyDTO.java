package grupo12.practico.dtos.AccessPolicy;

public class AddHealthWorkerAccessPolicyDTO {
    private String healthUserId;
    private String healthWorkerCi;
    private String clinicName;
    private String accessRequestId;

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public String getHealthWorkerCi() {
        return healthWorkerCi;
    }

    public void setHealthWorkerCi(String healthWorkerCi) {
        this.healthWorkerCi = healthWorkerCi;
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
