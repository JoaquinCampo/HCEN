package grupo12.practico.repositories.AccessGrant;

import jakarta.ejb.Local;

import java.util.List;

import grupo12.practico.models.AccessGrant;

@Local
public interface AccessGrantRepositoryLocal {
    AccessGrant add(AccessGrant accessGrant);

    List<AccessGrant> findAll();

    AccessGrant findById(String id);

    List<AccessGrant> findByClinicalHistoryId(String clinicalHistoryId);

    List<AccessGrant> findBySubject(String subjectType, String subjectId);

    List<AccessGrant> findActiveByClinicalHistoryId(String clinicalHistoryId);

    void revokeAccessGrant(String id);
}
