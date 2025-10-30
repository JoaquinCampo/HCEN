package grupo12.practico.repositories.AccessRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import grupo12.practico.models.AccessRequest;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;

@Stateless
@Local(AccessRequestRepositoryLocal.class)
@Remote(AccessRequestRepositoryRemote.class)
public class AccessRequestRepositoryBean implements AccessRequestRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public AccessRequest add(AccessRequest accessRequest) {
        if (accessRequest == null) {
            throw new ValidationException("Access request must not be null");
        }
        em.persist(accessRequest);
        return accessRequest;
    }

    @Override
    public AccessRequest findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        return em.find(AccessRequest.class, id);
    }

    @Override
    public Optional<AccessRequest> findExisting(String healthUserId, String healthWorkerCi, String clinicName) {
        if (healthUserId == null || healthUserId.trim().isEmpty()
                || healthWorkerCi == null || healthWorkerCi.trim().isEmpty()
                || clinicName == null || clinicName.trim().isEmpty()) {
            return Optional.empty();
        }

        TypedQuery<AccessRequest> query = em.createQuery(
                "SELECT ar FROM AccessRequest ar WHERE ar.healthUser.id = :healthUserId "
                        + "AND ar.healthWorkerCi = :healthWorkerCi "
                        + "AND ar.clinicName = :clinicName",
                AccessRequest.class);
        query.setParameter("healthUserId", healthUserId);
        query.setParameter("healthWorkerCi", healthWorkerCi);
        query.setParameter("clinicName", clinicName);

        try {
            AccessRequest result = query.setMaxResults(1).getSingleResult();
            return Optional.of(result);
        } catch (NoResultException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(AccessRequest accessRequest) {
        if (accessRequest == null) {
            return;
        }
        AccessRequest managed = accessRequest;
        if (!em.contains(accessRequest)) {
            managed = em.merge(accessRequest);
        }
        em.remove(managed);
    }

    @Override
    public List<AccessRequest> findAllByHealthUserId(String healthUserId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<AccessRequest> query = em.createQuery(
                "SELECT ar FROM AccessRequest ar WHERE ar.healthUser.id = :healthUserId", AccessRequest.class);
        query.setParameter("healthUserId", healthUserId);
        return query.getResultList();
    }
}
