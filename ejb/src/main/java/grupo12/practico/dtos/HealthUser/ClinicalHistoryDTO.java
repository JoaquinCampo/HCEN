package grupo12.practico.dtos.HealthUser;

import java.io.Serializable;

/**
 * Simple DTO that wraps the clinical history payload returned by the external
 * service.
 */
public class ClinicalHistoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String healthUserId;
    private String payload;

    public ClinicalHistoryDTO() {
    }

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
