package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;

public class CreateClinicalDocumentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String healthWorkerCi;
    private String healthUserCi;
    private String clinicName;
    private String s3Url;

    public String getHealthWorkerCi() {
        return healthWorkerCi;
    }

    public void setHealthWorkerCi(String healthWorkerCi) {
        this.healthWorkerCi = healthWorkerCi;
    }

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

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
}
