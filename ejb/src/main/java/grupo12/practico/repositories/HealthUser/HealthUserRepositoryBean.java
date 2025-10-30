package grupo12.practico.repositories.HealthUser;

import jakarta.ejb.Local;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

import grupo12.practico.models.HealthUser;

import jakarta.validation.ValidationException;

@Stateless
@Local(HealthUserRepositoryLocal.class)
@Remote(HealthUserRepositoryRemote.class)
public class HealthUserRepositoryBean implements HealthUserRepositoryRemote {

    @PersistenceContext(unitName = "practicoPersistenceUnit")
    private EntityManager em;

    @Override
    public List<HealthUser> findAll(String clinicName, String name, String ci, Integer pageIndex, Integer pageSize) {
        String trimmedClinic = clinicName != null ? clinicName.trim() : null;
        String trimmedName = name != null ? name.trim() : null;
        String trimmedCi = ci != null ? ci.trim() : null;

        StringBuilder jpql = new StringBuilder("SELECT DISTINCT h FROM HealthUser h");

        boolean filterByClinic = trimmedClinic != null && !trimmedClinic.isEmpty();
        boolean filterByName = trimmedName != null && !trimmedName.isEmpty();
        boolean filterByCi = trimmedCi != null && !trimmedCi.isEmpty();

        if (filterByClinic) {
            jpql.append(" JOIN h.clinicNames clinic");
        }

        boolean hasCondition = false;
        if (filterByCi || filterByClinic || filterByName) {
            jpql.append(" WHERE");
        }

        if (filterByCi) {
            jpql.append(" LOWER(h.ci) LIKE :ci");
            hasCondition = true;
        }

        if (filterByClinic) {
            if (hasCondition) {
                jpql.append(" AND");
            }
            jpql.append(" LOWER(clinic) LIKE :clinic");
            hasCondition = true;
        }

        if (filterByName) {
            if (hasCondition) {
                jpql.append(" AND");
            }
            jpql.append(
                    " LOWER(CONCAT(CONCAT(COALESCE(h.firstName, ''), ' '), COALESCE(h.lastName, ''))) LIKE :name");
        }

        jpql.append(" ORDER BY h.lastName ASC, h.firstName ASC, h.id ASC");

        TypedQuery<HealthUser> query = em.createQuery(jpql.toString(), HealthUser.class);

        if (filterByCi) {
            query.setParameter("ci", "%" + trimmedCi.toLowerCase() + "%");
        }

        if (filterByClinic) {
            query.setParameter("clinic", "%" + trimmedClinic.toLowerCase() + "%");
        }

        if (filterByName) {
            query.setParameter("name", "%" + trimmedName.toLowerCase() + "%");
        }

        if (pageSize != null && pageSize > 0) {
            int safePageIndex = pageIndex != null && pageIndex >= 0 ? pageIndex : 0;
            query.setFirstResult(safePageIndex * pageSize);
            query.setMaxResults(pageSize);
        }

        return query.getResultList();
    }

    @Override
    public HealthUser findHealthUserByCi(String healthUserCi) {
        if (healthUserCi == null || healthUserCi.trim().isEmpty()) {
            throw new ValidationException("Health user CI must not be null or empty");
        }
        return em.createQuery("SELECT h FROM HealthUser h WHERE h.ci = :ci", HealthUser.class)
                .setParameter("ci", healthUserCi)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public HealthUser addClinicToHealthUser(String healthUserCi, String clinicName) {
        HealthUser healthUser = findHealthUserByCi(healthUserCi);
        boolean alreadyLinked = healthUser.getClinicNames().stream()
                .anyMatch(existing -> existing != null && existing.equalsIgnoreCase(clinicName));

        if (!alreadyLinked) {
            healthUser.getClinicNames().add(clinicName);
            em.merge(healthUser);
        }
        return healthUser;
    }

    @Override
    public HealthUser add(HealthUser healthUser) {
        if (healthUser == null) {
            throw new ValidationException("HealthUser must not be null");
        }
        em.persist(healthUser);
        return healthUser;
    }
}
