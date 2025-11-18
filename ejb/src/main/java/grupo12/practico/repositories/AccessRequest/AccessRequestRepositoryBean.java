package grupo12.practico.repositories.AccessRequest;

import java.util.List;

import grupo12.practico.models.AccessRequest;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
@Local(AccessRequestRepositoryLocal.class)
@Remote(AccessRequestRepositoryRemote.class)
public class AccessRequestRepositoryBean implements AccessRequestRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public AccessRequest createAccessRequest(AccessRequest accessRequest) {
        em.persist(accessRequest);
        return accessRequest;
    }

    @Override
    public AccessRequest findAccessRequestById(String id) {
        TypedQuery<AccessRequest> query = em.createQuery(
                "SELECT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE ar.id = :id",
                AccessRequest.class);
        query.setParameter("id", id);
        List<AccessRequest> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<AccessRequest> findAllAccessRequests(String healthUserId, String healthWorkerCi, String clinicName) {
        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT ar FROM AccessRequest ar LEFT JOIN FETCH ar.specialtyNames WHERE 1=1");

        if (healthUserId != null) {
            jpql.append(" AND ar.healthUser.id = :healthUserId");
        }
        if (healthWorkerCi != null) {
            jpql.append(" AND ar.healthWorkerCi = :healthWorkerCi");
        }
        if (clinicName != null) {
            jpql.append(" AND ar.clinicName = :clinicName");
        }

        TypedQuery<AccessRequest> query = em.createQuery(jpql.toString(), AccessRequest.class);

        if (healthUserId != null) {
            query.setParameter("healthUserId", healthUserId);
        }
        if (healthWorkerCi != null) {
            query.setParameter("healthWorkerCi", healthWorkerCi);
        }
        if (clinicName != null) {
            query.setParameter("clinicName", clinicName);
        }

        return query.getResultList();
    }

    @Override
    public void deleteAccessRequest(String accessRequestId) {
        AccessRequest accessRequest = em.find(AccessRequest.class, accessRequestId);

        if (accessRequest != null) {
            em.remove(accessRequest);
        }
    }
}