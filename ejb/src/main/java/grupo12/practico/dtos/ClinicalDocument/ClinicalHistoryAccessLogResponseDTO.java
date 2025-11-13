package grupo12.practico.dtos.ClinicalDocument;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ClinicalHistoryAccessLogResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String healthUserCi;
    private String healthWorkerCi;
    private String clinicName;
    private LocalDateTime requestedAt;
    private Boolean viewed;
    private String decisionReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }
}
