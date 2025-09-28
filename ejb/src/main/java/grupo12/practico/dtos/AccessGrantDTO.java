package grupo12.practico.dtos;

import java.io.Serializable;
import java.time.LocalDate;

public class AccessGrantDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String clinicalHistoryId;
    private String subjectType;
    private String subjectId;
    private String scope;
    private LocalDate startsAt;
    private LocalDate endsAt;
    private String grantedBy;
    private String reason;
    private LocalDate createdAt;
    private LocalDate revokedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClinicalHistoryId() {
        return clinicalHistoryId;
    }

    public void setClinicalHistoryId(String clinicalHistoryId) {
        this.clinicalHistoryId = clinicalHistoryId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public LocalDate getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDate startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDate getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDate endsAt) {
        this.endsAt = endsAt;
    }

    public String getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(String grantedBy) {
        this.grantedBy = grantedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDate revokedAt) {
        this.revokedAt = revokedAt;
    }

    /**
     * Checks if this access grant is currently active.
     */
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        if (revokedAt != null) {
            return false;
        }
        if (startsAt != null && now.isBefore(startsAt)) {
            return false;
        }
        if (endsAt != null && now.isAfter(endsAt)) {
            return false;
        }
        return true;
    }
}
