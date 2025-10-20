package grupo12.practico.repositories.ClinicAccessPolicy;

import java.util.Optional;

import grupo12.practico.models.ClinicAccessPolicy;
import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ValidationException;

@Stateless
@Local(ClinicAccessPolicyRepositoryLocal.class)
@Remote(ClinicAccessPolicyRepositoryRemote.class)
public class ClinicAccessPolicyRepositoryBean implements ClinicAccessPolicyRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public ClinicAccessPolicy add(ClinicAccessPolicy policy) {
        if (policy == null) {
            throw new ValidationException("Clinic access policy must not be null");
        }
        em.persist(policy);
        return policy;
    }

    @Override
    public Optional<ClinicAccessPolicy> findByHealthUserAndClinic(String healthUserId, String clinicId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()
                || clinicId == null || clinicId.trim().isEmpty()) {
            return Optional.empty();
        }

        TypedQuery<ClinicAccessPolicy> query = em.createQuery(
                "SELECT p FROM ClinicAccessPolicy p WHERE p.healthUser.id = :healthUserId "
                        + "AND p.clinic.id = :clinicId",
                ClinicAccessPolicy.class);
        query.setParameter("healthUserId", healthUserId);
        query.setParameter("clinicId", clinicId);

        try {
            return Optional.of(query.setMaxResults(1).getSingleResult());
        } catch (NoResultException ignored) {
            return Optional.empty();
        }
    }
}
