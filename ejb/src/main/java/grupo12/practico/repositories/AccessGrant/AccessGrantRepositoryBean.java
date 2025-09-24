package grupo12.practico.repositories.AccessGrant;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import grupo12.practico.models.AccessGrant;

@Singleton
@Startup
@Local(AccessGrantRepositoryLocal.class)
@Remote(AccessGrantRepositoryRemote.class)
public class AccessGrantRepositoryBean implements AccessGrantRepositoryRemote {

    private final Map<String, AccessGrant> accessGrants = new HashMap<>();

    @Override
    public AccessGrant add(AccessGrant accessGrant) {
        if (accessGrant == null || accessGrant.getId() == null)
            return accessGrant;
        accessGrants.put(accessGrant.getId(), accessGrant);
        return accessGrant;
    }

    @Override
    public List<AccessGrant> findAll() {
        return new ArrayList<>(accessGrants.values());
    }

    @Override
    public AccessGrant findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return accessGrants.get(id);
    }

    @Override
    public List<AccessGrant> findByClinicalHistoryId(String clinicalHistoryId) {
        if (clinicalHistoryId == null || clinicalHistoryId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return accessGrants.values().stream()
                .filter(grant -> clinicalHistoryId.equals(grant.getClinicalHistoryId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AccessGrant> findBySubject(String subjectType, String subjectId) {
        if (subjectType == null || subjectId == null) {
            return new ArrayList<>();
        }
        return accessGrants.values().stream()
                .filter(grant -> subjectType.equals(grant.getSubjectType()) && subjectId.equals(grant.getSubjectId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AccessGrant> findActiveByClinicalHistoryId(String clinicalHistoryId) {
        return findByClinicalHistoryId(clinicalHistoryId).stream()
                .filter(AccessGrant::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeAccessGrant(String id) {
        AccessGrant grant = accessGrants.get(id);
        if (grant != null) {
            grant.revoke();
        }
    }
}
