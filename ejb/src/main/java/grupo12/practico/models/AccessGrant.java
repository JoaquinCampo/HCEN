package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import grupo12.practico.dto.AccessGrantDTO;

/**
 * Represents access permissions for clinical histories.
 * Can grant access to specific clinics, specialties, or individual workers.
 */
public class AccessGrant {
    private String id;
    private String clinicalHistoryId;

    // Polymorphic subject: either clinic, specialty, or worker
    private String subjectType; // "CLINIC", "SPECIALTY", "WORKER"
    private String subjectId; // ID of the clinic, specialty, or worker

    private String scope; // e.g., "READ", "READ_WRITE", "ADMIN"
    private LocalDate startsAt;
    private LocalDate endsAt;

    private String grantedBy; // ID of the user who granted access
    private String reason; // Reason for granting access

    private LocalDate createdAt;
    private LocalDate revokedAt;

    public AccessGrant() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.scope = "READ"; // Default scope
    }

    // Getters and setters
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

    /**
     * Revokes this access grant.
     */
    public void revoke() {
        this.revokedAt = LocalDate.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AccessGrant that = (AccessGrant) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AccessGrant{" +
                "id='" + id + '\'' +
                ", clinicalHistoryId='" + clinicalHistoryId + '\'' +
                ", subjectType='" + subjectType + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", scope='" + scope + '\'' +
                ", startsAt=" + startsAt +
                ", endsAt=" + endsAt +
                ", grantedBy='" + grantedBy + '\'' +
                ", reason='" + reason + '\'' +
                ", revokedAt=" + revokedAt +
                '}';
    }

    public AccessGrantDTO toDto() {
        AccessGrantDTO dto = new AccessGrantDTO();
        dto.setId(id);
        dto.setClinicalHistoryId(clinicalHistoryId);
        dto.setSubjectType(subjectType);
        dto.setSubjectId(subjectId);
        dto.setScope(scope);
        dto.setStartsAt(startsAt);
        dto.setEndsAt(endsAt);
        dto.setGrantedBy(grantedBy);
        dto.setReason(reason);
        dto.setCreatedAt(createdAt);
        dto.setRevokedAt(revokedAt);
        return dto;
    }
}
