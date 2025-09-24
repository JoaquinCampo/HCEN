package grupo12.practico.services.AccessGrant;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.AccessGrant;

@Local
public interface AccessGrantServiceLocal {
    AccessGrant grantAccess(AccessGrant accessGrant);

    List<AccessGrant> findAll();

    AccessGrant findById(String id);

    List<AccessGrant> findByClinicalHistory(String clinicalHistoryId);

    List<AccessGrant> findActiveByClinicalHistory(String clinicalHistoryId);

    boolean hasAccess(String clinicalHistoryId, String subjectType, String subjectId);

    void revokeAccess(String accessGrantId);

    List<AccessGrant> findBySubject(String subjectType, String subjectId);
}
