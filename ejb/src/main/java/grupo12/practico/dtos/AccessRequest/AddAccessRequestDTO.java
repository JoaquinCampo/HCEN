package grupo12.practico.dtos.AccessRequest;

import java.io.Serializable;

public class AddAccessRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String healthUserCi;
    private String healthWorkerCi;
    private String clinicName;

    public String getHealthUserCi() {
        return healthUserCi;
    }

    public void setHealthUserCi(String healthUserCi) {
        this.healthUserCi = healthUserCi;
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

}
