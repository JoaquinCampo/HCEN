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
    public AccessRequest create(AccessRequest accessRequest) {
        em.persist(accessRequest);
        return accessRequest;
    }

    @Override
    public AccessRequest findById(String id) {
        return em.find(AccessRequest.class, id);
    }

    @Override
    public List<AccessRequest> findAll(String healthUserId, String healthWorkerCi, String clinicName) {
        StringBuilder jpql = new StringBuilder("SELECT ar FROM AccessRequest ar WHERE 1=1");

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
    public void delete(String accessRequestId) {
        AccessRequest accessRequest = em.find(AccessRequest.class, accessRequestId);
        if (accessRequest != null) {
            em.remove(accessRequest);
        }
    }
}