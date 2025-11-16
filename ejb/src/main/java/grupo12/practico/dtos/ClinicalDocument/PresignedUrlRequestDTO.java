package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;
import java.util.List;

public class PresignedUrlRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private String contentType;
    private String healthUserCi;
    private String healthWorkerCi;
    private String clinicName;
    private String providerName;
    private List<String> specialtyNames;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

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

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public List<String> getSpecialtyNames() {
        return specialtyNames;
    }

    public void setSpecialtyNames(List<String> specialtyNames) {
        this.specialtyNames = specialtyNames;
    }
}
