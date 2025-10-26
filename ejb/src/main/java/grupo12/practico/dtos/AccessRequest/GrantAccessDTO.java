package grupo12.practico.dtos.AccessRequest;

import java.io.Serializable;

public class GrantAccessDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String accessRequestId;
    private String healthUserId;

    public String getAccessRequestId() {
        return accessRequestId;
    }

    public void setAccessRequestId(String accessRequestId) {
        this.accessRequestId = accessRequestId;
    }

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }
}
