package grupo12.practico.repositories.HealthWorkerAccessPolicy;

import java.util.Optional;

import grupo12.practico.models.HealthWorkerAccessPolicy;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;

@Stateless
@Local(HealthWorkerAccessPolicyRepositoryLocal.class)
@Remote(HealthWorkerAccessPolicyRepositoryRemote.class)
public class HealthWorkerAccessPolicyRepositoryBean implements HealthWorkerAccessPolicyRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public HealthWorkerAccessPolicy add(HealthWorkerAccessPolicy policy) {
        if (policy == null) {
            throw new ValidationException("Health worker access policy must not be null");
        }
        em.persist(policy);
        return policy;
    }

    @Override
    public Optional<HealthWorkerAccessPolicy> findByHealthUserAndHealthWorker(String healthUserId,
            String healthWorkerId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()
                || healthWorkerId == null || healthWorkerId.trim().isEmpty()) {
            return Optional.empty();
        }

        TypedQuery<HealthWorkerAccessPolicy> query = em.createQuery(
                "SELECT p FROM HealthWorkerAccessPolicy p WHERE p.healthUser.id = :healthUserId "
                        + "AND p.healthWorker.id = :healthWorkerId",
                HealthWorkerAccessPolicy.class);

        query.setParameter("healthUserId", healthUserId);
        query.setParameter("healthWorkerId", healthWorkerId);

        try {
            return Optional.of(query.setMaxResults(1).getSingleResult());
        } catch (NoResultException ignored) {
            return Optional.empty();
        }
    }
}
