package grupo12.practico.dtos.AccessRequest;

import java.io.Serializable;
import java.time.LocalDate;

public class GrantAccessResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean accepted;
    private String message;
    private String policyId;
    private String healthUserId;
    private String targetType;
    private String targetId;
    private LocalDate grantedAt;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getHealthUserId() {
        return healthUserId;
    }

    public void setHealthUserId(String healthUserId) {
        this.healthUserId = healthUserId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public LocalDate getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(LocalDate grantedAt) {
        this.grantedAt = grantedAt;
    }

    public static GrantAccessResultDTO accepted(String policyId, String healthUserId, String targetType,
            String targetId, LocalDate grantedAt) {
        GrantAccessResultDTO dto = new GrantAccessResultDTO();
        dto.setAccepted(true);
        dto.setMessage("Access granted");
        dto.setPolicyId(policyId);
        dto.setHealthUserId(healthUserId);
        dto.setTargetType(targetType);
        dto.setTargetId(targetId);
        dto.setGrantedAt(grantedAt);
        return dto;
    }

    public static GrantAccessResultDTO denied(String healthUserId, String targetType, String targetId) {
        GrantAccessResultDTO dto = new GrantAccessResultDTO();
        dto.setAccepted(false);
        dto.setMessage("Access request denied");
        dto.setHealthUserId(healthUserId);
        dto.setTargetType(targetType);
        dto.setTargetId(targetId);
        return dto;
    }
}
