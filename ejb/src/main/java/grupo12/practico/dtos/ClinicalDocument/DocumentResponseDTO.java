package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DocumentResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String docId;
    private String healthWorkerCI;
    private String healthUserCi;
    private String clinicName;
    private LocalDateTime createdAt;
    private String s3Url;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getHealthWorkerCI() {
        return healthWorkerCI;
    }

    public void setHealthWorkerCI(String healthWorkerCI) {
        this.healthWorkerCI = healthWorkerCI;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
}
