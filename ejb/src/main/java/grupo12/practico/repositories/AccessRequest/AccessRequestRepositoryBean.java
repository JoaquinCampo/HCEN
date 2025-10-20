package grupo12.practico.repositories.AccessRequest;

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
    public Optional<AccessRequest> findExisting(String healthUserId, String healthWorkerId, String clinicId,
            String specialtyId) {
        if (healthUserId == null || healthUserId.trim().isEmpty()
                || healthWorkerId == null || healthWorkerId.trim().isEmpty()
                || clinicId == null || clinicId.trim().isEmpty()
                || specialtyId == null || specialtyId.trim().isEmpty()) {
            return Optional.empty();
        }

        TypedQuery<AccessRequest> query = em.createQuery(
                "SELECT ar FROM AccessRequest ar WHERE ar.healthUser.id = :healthUserId "
                        + "AND ar.healthWorker.id = :healthWorkerId "
                        + "AND ar.clinic.id = :clinicId "
                        + "AND ar.specialty.id = :specialtyId",
                AccessRequest.class);
        query.setParameter("healthUserId", healthUserId);
        query.setParameter("healthWorkerId", healthWorkerId);
        query.setParameter("clinicId", clinicId);
        query.setParameter("specialtyId", specialtyId);

        try {
            AccessRequest result = query.setMaxResults(1).getSingleResult();
            return Optional.of(result);
        } catch (NoResultException ignored) {
            return Optional.empty();
        }
    }
}
