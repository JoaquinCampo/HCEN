package grupo12.practico.services.AccessGrant;

import grupo12.practico.models.AccessGrant;
import grupo12.practico.repositories.AccessGrant.AccessGrantRepositoryLocal;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.validation.ValidationException;

import java.util.List;

@Stateless
@Local(AccessGrantServiceLocal.class)
@Remote(AccessGrantServiceRemote.class)
public class AccessGrantServiceBean implements AccessGrantServiceRemote {

    @EJB
    private AccessGrantRepositoryLocal repository;

    @Override
    public AccessGrant grantAccess(AccessGrant accessGrant) {
        validateAccessGrant(accessGrant);
        return repository.add(accessGrant);
    }

    @Override
    public List<AccessGrant> findAll() {
        return repository.findAll();
    }

    @Override
    public AccessGrant findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<AccessGrant> findByClinicalHistory(String clinicalHistoryId) {
        return repository.findByClinicalHistoryId(clinicalHistoryId);
    }

    @Override
    public List<AccessGrant> findActiveByClinicalHistory(String clinicalHistoryId) {
        return repository.findActiveByClinicalHistoryId(clinicalHistoryId);
    }

    @Override
    public boolean hasAccess(String clinicalHistoryId, String subjectType, String subjectId) {
        List<AccessGrant> activeGrants = findActiveByClinicalHistory(clinicalHistoryId);
        return activeGrants.stream()
                .anyMatch(
                        grant -> subjectType.equals(grant.getSubjectType()) && subjectId.equals(grant.getSubjectId()));
    }

    @Override
    public void revokeAccess(String accessGrantId) {
        repository.revokeAccessGrant(accessGrantId);
    }

    @Override
    public List<AccessGrant> findBySubject(String subjectType, String subjectId) {
        return repository.findBySubject(subjectType, subjectId);
    }

    private void validateAccessGrant(AccessGrant grant) {
        if (grant == null) {
            throw new ValidationException("AccessGrant must not be null");
        }
        if (isBlank(grant.getClinicalHistoryId())) {
            throw new ValidationException("Clinical history ID is required");
        }
        if (isBlank(grant.getSubjectType())) {
            throw new ValidationException("Subject type is required");
        }
        if (isBlank(grant.getSubjectId())) {
            throw new ValidationException("Subject ID is required");
        }
        if (!isValidSubjectType(grant.getSubjectType())) {
            throw new ValidationException("Invalid subject type. Must be CLINIC, SPECIALTY, or WORKER");
        }
        if (isBlank(grant.getGrantedBy())) {
            throw new ValidationException("Granted by user ID is required");
        }
    }

    private boolean isValidSubjectType(String subjectType) {
        return "CLINIC".equals(subjectType) || "SPECIALTY".equals(subjectType) || "WORKER".equals(subjectType);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
