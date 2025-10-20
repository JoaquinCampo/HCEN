package grupo12.practico.repositories.SpecialtyAccessPolicy;

import java.util.Optional;

import grupo12.practico.models.SpecialtyAccessPolicy;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;

@Stateless
@Local(SpecialtyAccessPolicyRepositoryLocal.class)
@Remote(SpecialtyAccessPolicyRepositoryRemote.class)
public class SpecialtyAccessPolicyRepositoryBean implements SpecialtyAccessPolicyRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public SpecialtyAccessPolicy add(SpecialtyAccessPolicy policy) {
        if (policy == null) {
            throw new ValidationException("Specialty access policy must not be null");
        }
        em.persist(policy);
        return policy;
    }

    @Override
    public Optional<SpecialtyAccessPolicy> findByHealthUserAndSpecialty(String healthUserId, String specialtyId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()
                || specialtyId == null || specialtyId.trim().isEmpty()) {
            return Optional.empty();
        }

        TypedQuery<SpecialtyAccessPolicy> query = em.createQuery(
                "SELECT p FROM SpecialtyAccessPolicy p WHERE p.healthUser.id = :healthUserId "
                        + "AND p.specialty.id = :specialtyId",
                SpecialtyAccessPolicy.class);
        query.setParameter("healthUserId", healthUserId);
        query.setParameter("specialtyId", specialtyId);

        try {
            return Optional.of(query.setMaxResults(1).getSingleResult());
        } catch (NoResultException ignored) {
            return Optional.empty();
        }
    }
}
